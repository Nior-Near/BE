package nior_near.server.domain.store.exception.handler;


import nior_near.server.global.common.ResponseCode;
import nior_near.server.global.error.GeneralException;

public class StoreHandler extends GeneralException {

    public StoreHandler(ResponseCode errorCode) {
        super(errorCode);
    }

}
