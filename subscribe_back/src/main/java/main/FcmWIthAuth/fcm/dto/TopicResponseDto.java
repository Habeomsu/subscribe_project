package main.FcmWIthAuth.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class TopicResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopicDto{

        private Long id;
        private String topic;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchTopicDto{

        List<TopicResponseDto.TopicDto> topics;
        boolean isFirst;
        boolean isLast;
        int listSize;
        long totalElements;

    }

}
