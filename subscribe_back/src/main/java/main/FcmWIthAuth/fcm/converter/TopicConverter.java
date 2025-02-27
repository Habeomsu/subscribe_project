package main.FcmWIthAuth.fcm.converter;

import main.FcmWIthAuth.fcm.dto.TopicResponseDto;
import main.FcmWIthAuth.fcm.entity.Topic;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class TopicConverter {

    public static TopicResponseDto.TopicDto toDto(Topic topic) {
        return TopicResponseDto.TopicDto.builder()
                .id(topic.getId())
                .topic(topic.getTopicName())
                .build();
    }

    public static List<TopicResponseDto.TopicDto> toDto(List<Topic> topics) {
        return topics.stream()
                .map(TopicConverter::toDto)
                .collect(Collectors.toList());
    }

    public static TopicResponseDto.SearchTopicDto toSearchDto(Page<Topic> topics) {
        return TopicResponseDto.SearchTopicDto.builder()
                .topics(toDto(topics.getContent()))
                .isFirst(topics.isFirst())
                .isLast(topics.isLast())
                .listSize(topics.getTotalPages())
                .totalElements(topics.getTotalElements())
                .build();
    }

}
