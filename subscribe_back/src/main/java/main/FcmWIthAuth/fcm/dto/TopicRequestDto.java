package main.FcmWIthAuth.fcm.dto;

import lombok.Getter;

public class TopicRequestDto {

    @Getter
    public static class TopicDto{

        private String topic;
    }

    @Getter
    public static class TopicMessageDto{
        private String title;

        private String content;

        private String url;

        private String img;

        private String topic;

    }
}
