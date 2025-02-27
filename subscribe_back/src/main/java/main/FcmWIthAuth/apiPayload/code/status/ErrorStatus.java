package main.FcmWIthAuth.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.FcmWIthAuth.apiPayload.code.BaseErrorCode;
import main.FcmWIthAuth.apiPayload.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 상태
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"COMMON500","서버 에러"),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다"),
    _FORBIDDEN(HttpStatus.FORBIDDEN,"COMMON402","금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND,"COMMON403","데이터를 찾지 못했습니다."),

    // 토큰
    _EXFIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"JWT400_1","만료된 access 토큰입니다."),
    _INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"JWT400_2","유효하지 않는 access 토큰입니다."),
    _ACCESS_TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED,"JWT400_3","access 토큰의 서명이 일치하지 않습니다."),
    _NOTFOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"JWT400_4","refresh 토큰이 존재하지않습니다."),
    _EXFIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"JWT400_5","만료된 refresh 토큰입니다."),
    _INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"JWT400_6","유효하지 않는 refresh 토큰입니다."),
    _NOFOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"JWT400_7","DB에 refresh 토큰이 존재하지 않습니다."),
    _REFRESH_TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED,"JWT400_8","refresh 토큰의 서명이 일치하지 않습니다."),

    //username
    _EXIST_USERNAME(HttpStatus.BAD_REQUEST,"USER400_1","아이디가 존재합니다."),
    _USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND,"USER400_2","회원가입된 아이디가 아닙니다."),
    _NOT_MATCH_EMAIL(HttpStatus.BAD_REQUEST,"USER400_3","인증번호가 일치하지 않습니다."),

    //fcm
    _THREAD_INTERRUPTED(HttpStatus.BAD_REQUEST,"FCM400_1","구독중 쓰레드 오류"),
    _SUBSCRIBE_ERROR(HttpStatus.BAD_REQUEST,"FCM400_2","구독 에러"),
    _UNSUBSCRIBE_ERROR(HttpStatus.BAD_REQUEST,"FCM400_3","구독 취소 에러"),
    _NOT_FOUND_TOPIC(HttpStatus.NOT_FOUND,"FCM400_4","토픽이 없습니다."),
    _ALREADY_SUBSCRIBE_TOPIC(HttpStatus.BAD_REQUEST,"FCM400_5","이미 구독중입니다."),
    _NOT_SEND_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR,"FCM400_6","메시지 전송 오류"),
    _ALREADY_EXIST_TOPIC(HttpStatus.BAD_REQUEST,"FCM400_7","이미 토픽이 존재합니다."),
    _NOT_MATCH_TOKEN_USER(HttpStatus.BAD_REQUEST,"FCM400_8","토큰의 사용자가 일치하지않습니다."),
    _TOPIC_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST,"FCM400_9","토큰과 토픽이 일치하지않습니다."),
    _NOT_SUBSCRIBED_TOPIC(HttpStatus.BAD_REQUEST,"FCM400_10","구독중이지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .code(code)
                .message(message)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
