package main.AuthTemplate.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.AuthTemplate.apiPayload.code.BaseErrorCode;
import main.AuthTemplate.apiPayload.code.ErrorReasonDto;

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
