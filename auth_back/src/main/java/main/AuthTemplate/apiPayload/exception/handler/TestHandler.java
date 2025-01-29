package main.AuthTemplate.apiPayload.exception.handler;

import main.AuthTemplate.apiPayload.code.BaseErrorCode;
import main.AuthTemplate.apiPayload.exception.GeneralException;

public class TestHandler extends GeneralException {

    public TestHandler(BaseErrorCode code) {
        super(code);
    }

}
