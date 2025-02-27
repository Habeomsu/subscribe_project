package main.FcmWIthAuth.fcm.repository;

import jakarta.transaction.Transactional;
import main.FcmWIthAuth.fcm.entity.Token;
import main.FcmWIthAuth.fcm.entity.Topic;
import main.FcmWIthAuth.fcm.entity.TopicToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicTokenRepository extends JpaRepository<TopicToken, Long> {

    void deleteByTokenIn(List<Token> tokens);

    void deleteByTopic(Topic topic);
    List<TopicToken> findByTokenIn(List<Token> tokens);

    void deleteByTopicAndTokenIn(Topic topic, List<Token> tokens);

    @Transactional
    @Modifying
    @Query("DELETE FROM TopicToken tt WHERE tt.token.tokenValue IN :tokenValues")
    void deleteByTokenValueIn(@Param("tokenValues") List<String> tokenValues);

    Optional<TopicToken> findByTopicAndToken(Topic topic, Token token);


}
