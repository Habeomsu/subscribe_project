package main.FcmWIthAuth.fcm.controller;

import io.swagger.v3.oas.annotations.Operation;
import main.FcmWIthAuth.apiPayload.ApiResult;
import main.FcmWIthAuth.fcm.dto.TopicRequestDto;
import main.FcmWIthAuth.fcm.service.FcmService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FcmController {

    private final FcmService fcmService;

    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @Operation(summary = "구독자 메세지 전송",description = "POST (topic,title,content,img,url)")
    @PostMapping("/notification/topic")
    public ApiResult<?> sendMessageTopic(@RequestBody TopicRequestDto.TopicMessageDto topicMessageDto) {

        fcmService.sendByTopic(topicMessageDto);
        return ApiResult.onSuccess();

    }

}
