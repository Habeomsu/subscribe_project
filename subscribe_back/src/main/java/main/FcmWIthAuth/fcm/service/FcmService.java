package main.FcmWIthAuth.fcm.service;

import main.FcmWIthAuth.fcm.dto.TopicRequestDto;

import java.util.List;

public interface FcmService {
    // 구독
    void subscribeToTopic(String topic, List<String> tokens);

    //구독 취소
    void unsubscribeFromTopic(String topic, List<String> tokens);

    //구독 메세지 전송
    void sendByTopic(TopicRequestDto.TopicMessageDto topicMessageDto);


}
