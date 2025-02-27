package main.FcmWIthAuth.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.FcmWIthAuth.apiPayload.code.BaseErrorCode;
import main.FcmWIthAuth.apiPayload.code.ErrorReasonDto;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDto getErrorReason(){
        return this.code.getReason();
    }

    public ErrorReasonDto getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }

}
