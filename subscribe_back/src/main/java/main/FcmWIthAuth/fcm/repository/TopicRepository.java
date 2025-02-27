package main.FcmWIthAuth.fcm.repository;

import main.FcmWIthAuth.fcm.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByTopicName(String topicName);
    boolean existsByTopicName(String topicName);
}
