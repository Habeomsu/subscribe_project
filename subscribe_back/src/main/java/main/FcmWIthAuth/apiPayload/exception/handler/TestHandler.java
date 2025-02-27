package main.FcmWIthAuth.apiPayload.exception.handler;

import main.FcmWIthAuth.apiPayload.code.BaseErrorCode;
import main.FcmWIthAuth.apiPayload.exception.GeneralException;

public class TestHandler extends GeneralException {

    public TestHandler(BaseErrorCode code) {
        super(code);
    }

}
