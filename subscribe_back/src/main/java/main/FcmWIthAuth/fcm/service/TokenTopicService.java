package main.FcmWIthAuth.fcm.service;

import main.FcmWIthAuth.fcm.dto.TopicRequestDto;

import java.security.Principal;

public interface TokenTopicService {

    void saveFCMToken(String username, String fcmToken);
    void subscribeToTopics(String topicName, String username);
    void unsubscribeFromTopics(String topicName, String username);
    void unsubscribeExpiredTokens();
    void logoutAndUnsubscribe(String fcmToken,String username);
}
