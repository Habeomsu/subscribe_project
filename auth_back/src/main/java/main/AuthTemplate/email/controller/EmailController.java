package main.AuthTemplate.email.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import main.AuthTemplate.apiPayload.ApiResult;
import main.AuthTemplate.apiPayload.code.status.ErrorStatus;
import main.AuthTemplate.apiPayload.exception.GeneralException;
import main.AuthTemplate.email.dto.EmailRequestDto;
import main.AuthTemplate.email.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/email")
@Slf4j
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }



    @PostMapping("/signup")
    public ApiResult<?> mailSend(@RequestBody EmailRequestDto.EmailDto emailDto) {
        String code = emailService.joinEmail(emailDto.getEmail());
        log.info("code:",code);
        return ApiResult.onSuccess();
    }

    @PostMapping("/checking")
    public ApiResult<String> authCheck(@RequestBody @Valid EmailRequestDto.EmailCheckDto emailCheckDto) {
        Boolean checked = emailService.checkAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        if (checked) {
            return ApiResult.onSuccess("이메일 인증 성공");
        }
        else {
            throw new GeneralException(ErrorStatus._NOT_MATCH_EMAIL);
        }
    }

}
