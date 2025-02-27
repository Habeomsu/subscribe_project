package main.FcmWIthAuth.fcm.repository;

import main.FcmWIthAuth.fcm.entity.Topic;
import main.FcmWIthAuth.fcm.entity.TopicUser;
import main.FcmWIthAuth.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicUserRepository extends JpaRepository<TopicUser, Long> {

    boolean existsByTopicAndUser(Topic topic, User user);
    void deleteByTopicAndUser(Topic topic, User user);
    List<TopicUser> findByUser(User user);
}
