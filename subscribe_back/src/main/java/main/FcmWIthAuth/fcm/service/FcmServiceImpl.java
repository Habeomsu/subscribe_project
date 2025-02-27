package main.FcmWIthAuth.fcm.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.apiPayload.exception.GeneralException;
import main.FcmWIthAuth.fcm.dto.TopicRequestDto;
import main.FcmWIthAuth.fcm.repository.TokenRepository;
import main.FcmWIthAuth.fcm.repository.TopicTokenRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FcmServiceImpl implements FcmService {

    private final TokenRepository tokenRepository;
    private final TopicTokenRepository topicTokenRepository;

    public FcmServiceImpl(TokenRepository tokenRepository, TopicTokenRepository topicTokenRepository) {
        this.tokenRepository = tokenRepository;
        this.topicTokenRepository = topicTokenRepository;
    }



    @Override
    public void subscribeToTopic(String topic, List<String> tokens) {
        List<String> failedTokens = new ArrayList<>();

        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopicAsync(tokens, topic).get();
            log.info("구독하는 topic: " + topic);
            log.info(response.getSuccessCount() + "개의 토큰이 구독에 성공했습니다.");
            if (response.getFailureCount() > 0) {
                log.info(response.getFailureCount() + "개의 토큰이 구독을 실패했습니다.");
                response.getErrors().forEach(error -> {
                    failedTokens.add(tokens.get(error.getIndex()));
                    log.info("Error for token at index " + error.getIndex() + ": " + error.getReason() +
                            " (Token: " + failedTokens.get(error.getIndex()-1) + ")");
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Interrupted 상태를 유지
            throw new GeneralException(ErrorStatus._THREAD_INTERRUPTED);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause(); // 원래 발생한 예외 확인
            throw new GeneralException(ErrorStatus._THREAD_INTERRUPTED);
        }

        if (!failedTokens.isEmpty()) {
            log.warn("구독에 실패한 토큰입니다 " + failedTokens);
            topicTokenRepository.deleteByTokenValueIn(failedTokens);
            tokenRepository.deleteByTokenValueIn(failedTokens);
        }
    }

    @Override
    public void unsubscribeFromTopic(String topic, List<String> tokens) {
        List<String> failedTokens = new ArrayList<>();

        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(tokens, topic).get();
            log.info("구독 취소하는 topic: " + topic);
            log.info(response.getSuccessCount() + "개의 토큰이 구독 취소에 성공했습니다.");
            if (response.getFailureCount() > 0) {
                log.info(response.getFailureCount() + "개의 토큰이 구독 취소를 실패했습니다.");
                response.getErrors().forEach(error -> {
                    failedTokens.add(tokens.get(error.getIndex()));
                    log.info("Error for token at index " + error.getIndex() + ": " + error.getReason() +
                            " (Token: " + failedTokens.get(error.getIndex()-1) + ")");
                });

            }
        } catch (InterruptedException | ExecutionException e) {
            throw new GeneralException(ErrorStatus._UNSUBSCRIBE_ERROR);
        }

        if (!failedTokens.isEmpty()) {
            log.warn("구독 취소에 실패한 토큰입니다 " + failedTokens);
            tokenRepository.deleteByTokenValueIn(failedTokens);
        }

    }

    @Override
    public void sendByTopic(TopicRequestDto.TopicMessageDto topicMessageDto) {
        Message message = Message.builder()
                .setTopic(topicMessageDto.getTopic())
                .setNotification(Notification.builder()
                        .setTitle(topicMessageDto.getTitle())
                        .setBody(topicMessageDto.getContent())
                        .setImage(topicMessageDto.getImg())
                        .build())
                .putData("click_action",topicMessageDto.getUrl())
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new GeneralException(ErrorStatus._NOT_SEND_MESSAGE);
        }


    }
}
