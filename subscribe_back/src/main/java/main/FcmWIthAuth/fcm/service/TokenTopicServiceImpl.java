package main.FcmWIthAuth.fcm.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.apiPayload.exception.GeneralException;
import main.FcmWIthAuth.fcm.entity.Token;
import main.FcmWIthAuth.fcm.entity.Topic;
import main.FcmWIthAuth.fcm.entity.TopicToken;
import main.FcmWIthAuth.fcm.entity.TopicUser;
import main.FcmWIthAuth.fcm.repository.TokenRepository;
import main.FcmWIthAuth.fcm.repository.TopicRepository;
import main.FcmWIthAuth.fcm.repository.TopicTokenRepository;
import main.FcmWIthAuth.fcm.repository.TopicUserRepository;
import main.FcmWIthAuth.user.entity.User;
import main.FcmWIthAuth.user.repository.UserRepository;
import main.FcmWIthAuth.user.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TokenTopicServiceImpl implements TokenTopicService {

    // 토큰 만료 기간 상수 정의
    final int TOKEN_EXPIRATION_MONTHS = 2;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TopicUserRepository topicUserRepository;
    private final TopicRepository topicRepository;
    private final TopicTokenRepository topicTokenRepository;
    private final FcmService fcmService;

    public TokenTopicServiceImpl(UserRepository userRepository, TokenRepository tokenRepository,
                                 TopicUserRepository topicUserRepository, TopicRepository topicRepository,
                                 TopicTokenRepository topicTokenRepository, FcmService fcmService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.topicUserRepository = topicUserRepository;
        this.topicRepository = topicRepository;
        this.topicTokenRepository = topicTokenRepository;
        this.fcmService = fcmService;
    }

    @Override
    @Transactional
    public void saveFCMToken(String username, String fcmToken) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new GeneralException(ErrorStatus._USERNAME_NOT_FOUND);
        }
        Optional<Token> existingToken = tokenRepository.findByTokenValueAndUser(fcmToken, user);
        if (existingToken.isPresent()) {
            Token token = existingToken.get();
            log.info("이미 존재하는 토큰: " + existingToken.get().getTokenValue());
            token.setExpirationDate(LocalDate.now().plusMonths(2));
            tokenRepository.save(token);
        } else {
            // Only create and save a new token if it does not exist
            Token token = Token.builder()
                    .tokenValue(fcmToken)
                    .user(user)
                    .expirationDate(LocalDate.now().plusMonths(TOKEN_EXPIRATION_MONTHS))
                    .build();
            log.info("DB에 저장하는 token : " + token.getTokenValue());
            tokenRepository.save(token);

            // user에 토큰 저장
            user.getTokens().add(token);
            userRepository.save(user);

            // 사용자가 구독 중인 모든 토픽을 가져옴
            List<TopicUser> topicUsers= topicUserRepository.findByUser(user);
            List<Topic> subscribedTopics = topicUsers.stream()
                    .map(TopicUser::getTopic)
                    .distinct()
                    .collect(Collectors.toList());

            // 새 토큰을 기존에 구독된 모든 토픽과 매핑하여 TopicToken 생성 및 저장
            List<TopicToken> newSubscriptions = subscribedTopics.stream()
                    .map(topic -> new TopicToken(topic, token))
                    .collect(Collectors.toList());
            topicTokenRepository.saveAll(newSubscriptions);

            // 각 토픽에 대해 새 토큰 구독 처리
            for (Topic topic : subscribedTopics) {
                fcmService.subscribeToTopic(topic.getTopicName(), Collections.singletonList(token.getTokenValue()));
                log.info("새 토큰으로 " + topic.getTopicName() + " 토픽을 다시 구독합니다.");
            }
        }


    }

    //구독
    @Override
    @Transactional
    public void subscribeToTopics(String topicName, String username) {

        Topic topic = topicRepository.findByTopicName(topicName)
                .orElseThrow(()->new GeneralException(ErrorStatus._NOT_FOUND_TOPIC));

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new GeneralException(ErrorStatus._USERNAME_NOT_FOUND);
        }

        if (topicUserRepository.existsByTopicAndUser(topic, user)){
            throw new GeneralException(ErrorStatus._ALREADY_SUBSCRIBE_TOPIC);
        }

        List<Token> userTokens = user.getTokens();

        TopicUser topicUser = TopicUser.builder()
                .topic(topic)
                .user(user)
                .build();

        topicUserRepository.save(topicUser);

        // 토픽과 토큰을 매핑하여 저장 -> 사용자가 가지고 있는 토큰들이 topic을 구독
        List<TopicToken> topicTokens = userTokens.stream()
                .map(token -> new TopicToken(topic, token))
                .collect(Collectors.toList());
        topicTokenRepository.saveAll(topicTokens);

        // FCM 서비스를 사용하여 토픽에 대한 구독 진행
        List<String> tokenValues = userTokens.stream()
                .map(Token::getTokenValue)
                .collect(Collectors.toList());
        fcmService.subscribeToTopic(topicName, tokenValues);

    }

    //구독 취소
    @Override
    @Transactional
    public void unsubscribeFromTopics(String topicName, String username) {
        Topic topic = topicRepository.findByTopicName(topicName)
                .orElseThrow(()->new GeneralException(ErrorStatus._NOT_FOUND_TOPIC));

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new GeneralException(ErrorStatus._USERNAME_NOT_FOUND);
        }

//        if (!topicUserRepository.existsByTopicAndUser(topic, user)) {
//            throw new GeneralException(ErrorStatus._NOT_SUBSCRIBED_TOPIC);
//        }
        // 멤버가 구독하고 있는 해당 토픽을 찾아서 삭제
        topicUserRepository.deleteByTopicAndUser(topic, user);

        // 현재 사용자의 토큰 목록 가져오기
        List<Token> userTokens = user.getTokens();
        topicTokenRepository.deleteByTopicAndTokenIn(topic, userTokens);

        // FCM 서비스를 사용하여 토픽에 대한 구독 취소 진행
        List<String> tokenValues = userTokens.stream()
                .map(Token::getTokenValue)
                .collect(Collectors.toList());
        log.info(topicName + " 구독이 취소 되었습니다.");
        fcmService.unsubscribeFromTopic(topicName, tokenValues);

    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void unsubscribeExpiredTokens() {
        LocalDate now = LocalDate.now();
        log.info("오늘의 날짜 : " + now);

        // 만료된 토큰을 가져옵니다.
        List<Token> expiredTokens = tokenRepository.findByExpirationDate(now);

        // 만료된 토큰과 관련된 모든 TopicToken을 찾음
        List<TopicToken> topicTokens = topicTokenRepository.findByTokenIn(expiredTokens);

        // 만료된 토큰의 값들을 추출
        List<String> tokenValues = expiredTokens.stream()
                .map(Token::getTokenValue)
                .collect(Collectors.toList());

        // 각 TopicToken에 대해 구독 해지
        topicTokens.forEach(topicToken -> {
            fcmService.unsubscribeFromTopic(topicToken.getTopic().getTopicName(), tokenValues);
        });

        // 만료된 토큰 삭제
        topicTokenRepository.deleteAll(topicTokens); // TopicTokenRepository에서 먼저 삭제하고 TokenRepository에서 삭제
        tokenRepository.deleteAll(expiredTokens);
    }

    @Override
    @Transactional
    public void logoutAndUnsubscribe(String fcmToken, String username) {
        // 사용자 확인
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new GeneralException(ErrorStatus._USERNAME_NOT_FOUND);
        }

        // 토큰 확인
        Token token = tokenRepository.findByTokenValueAndUser(fcmToken,user)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_MATCH_TOKEN_USER));


        // 사용자가 구독한 모든 토픽을 가져옴
        List<TopicUser> topicUsers = topicUserRepository.findByUser(user);
        List<Topic> subscribedTopics = topicUsers.stream()
                .map(TopicUser::getTopic)
                .distinct()
                .collect(Collectors.toList());

        // 각 토픽에 대해 구독 취소 처리
        for (Topic topic : subscribedTopics) {
            fcmService.unsubscribeFromTopic(topic.getTopicName(), Collections.singletonList(token.getTokenValue()));
            log.info("토큰 {}으로 {} 토픽의 구독을 취소합니다.", fcmToken, topic.getTopicName());

            // TopicToken 삭제
            TopicToken topicToken = topicTokenRepository.findByTopicAndToken(topic, token)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._TOPIC_TOKEN_NOT_FOUND));
            topicTokenRepository.delete(topicToken);

            // Topic에서 TopicToken 리스트에서 삭제
            topic.getTopicTokens().remove(topicToken);
        }

        // 필요시 DB에서 토큰 삭제
        tokenRepository.delete(token);
    }

}
