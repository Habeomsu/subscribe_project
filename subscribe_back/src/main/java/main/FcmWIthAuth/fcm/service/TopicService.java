package main.FcmWIthAuth.fcm.service;

import main.FcmWIthAuth.fcm.dto.TopicResponseDto;
import main.FcmWIthAuth.fcm.entity.Topic;
import main.FcmWIthAuth.page.PostPagingDto;

public interface TopicService {

    void createTopic(String topicName);

    TopicResponseDto.SearchTopicDto getTopics(PostPagingDto.PagingDto pagingDto);


}
