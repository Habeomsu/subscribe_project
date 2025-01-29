package main.AuthTemplate.apiPayload.code;

public interface BaseErrorCode {
    public ErrorReasonDto getReason();
    public ErrorReasonDto getReasonHttpStatus();
}
