package main.FcmWIthAuth.fcm.controller;

import io.swagger.v3.oas.annotations.Operation;
import main.FcmWIthAuth.apiPayload.ApiResult;
import main.FcmWIthAuth.fcm.dto.TopicRequestDto;
import main.FcmWIthAuth.fcm.dto.TopicResponseDto;
import main.FcmWIthAuth.fcm.service.TokenTopicService;
import main.FcmWIthAuth.fcm.service.TopicService;
import main.FcmWIthAuth.page.PagingConverter;
import main.FcmWIthAuth.user.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    private final TokenTopicService tokenTopicService;
    private final TopicService topicService;

    public TopicController(TokenTopicService tokenTopicService, TopicService topicService) {
        this.tokenTopicService = tokenTopicService;
        this.topicService = topicService;
    }

    @Operation(summary = "구독 API",description = "POST (topic)")
    @PostMapping("/subscribe")
    public ApiResult<?> subscribeToTopic(@RequestBody TopicRequestDto.TopicDto topicDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        tokenTopicService.subscribeToTopics(topicDto.getTopic(), userDetails.getUsername());
        return ApiResult.onSuccess();
    }

    @Operation(summary = "구독 취소 API",description = "POST (topic)")

    @PostMapping("/unsubscribe")
    public ApiResult<?> unsubscribeFromTopic(@RequestBody TopicRequestDto.TopicDto topicDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        tokenTopicService.unsubscribeFromTopics(topicDto.getTopic(), userDetails.getUsername());
        return ApiResult.onSuccess();
    }

    @Operation(summary = "토픽 생성 API",description = "POST (topic)")
    @PostMapping
    public ApiResult<?> createTopic(@RequestBody TopicRequestDto.TopicDto topicDto) {

        topicService.createTopic(topicDto.getTopic());
        return ApiResult.onSuccess();
    }

    @Operation(summary = "모든 토픽 가져오기 API",description = "GET (page,size,sort)")
    @GetMapping
    public ApiResult<TopicResponseDto.SearchTopicDto> getTopics(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "desc") String sort){

        return ApiResult.onSuccess(topicService.getTopics(PagingConverter.toPagingDto(page, size, sort)));
    }



}
