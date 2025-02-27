package main.FcmWIthAuth.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.FcmWIthAuth.apiPayload.code.BaseCode;
import main.FcmWIthAuth.apiPayload.code.ReasonDto;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK,"COMMON200","성공입니다."),
    _CREATED(HttpStatus.OK,"COMMON201","생성 성공입니다"),
    _ACCEPTED(HttpStatus.OK,"COMMON202","처리 대기중입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
