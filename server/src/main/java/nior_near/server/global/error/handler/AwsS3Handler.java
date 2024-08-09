package nior_near.server.global.error.handler;

import nior_near.server.global.common.ResponseCode;
import nior_near.server.global.error.GeneralException;

public class AwsS3Handler extends GeneralException {

    public AwsS3Handler(ResponseCode errorCode) {
        super(errorCode);
    }

}
