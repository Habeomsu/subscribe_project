package main.FcmWIthAuth.apiPayload.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import main.FcmWIthAuth.apiPayload.ApiResult;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        log.info(String.valueOf(authException));
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = "인증 오류가 발생했습니다."; // 기본 메시지

        if (authException instanceof BadCredentialsException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "잘못된 자격 증명입니다."; // 잘못된 자격 증명
        } else if (authException instanceof DisabledException) {
            status = HttpStatus.FORBIDDEN;
            message = "계정이 비활성화되었습니다. 관리자에게 문의하세요."; // 비활성화된 계정
        } else if (authException instanceof LockedException) {
            status = HttpStatus.LOCKED;
            message = "계정이 잠겼습니다. 관리자에게 문의하세요."; // 잠긴 계정
        } else if (authException instanceof AccountExpiredException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "계정이 만료되었습니다. 관리자에게 문의하세요."; // 만료된 계정
        } else if (authException instanceof CredentialsExpiredException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "자격 증명이 만료되었습니다. 새 자격 증명을 입력하세요."; // 만료된 자격 증명
        }


        // 상태 코드 설정
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8"); // UTF-8 인코딩 설정

        // ApiResult 생성
        ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), message, null);

        // JSON 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(apiResult));
    }


}
