package com.ddm.server.websocket.handler.response;

import java.io.IOException;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.IBaseHandler;

public abstract class ResponseHandler extends IBaseHandler {
    public ResponseHandler() {
        super("WSResponseHandler");
    }

    public abstract void handleResponse(final WebSocketResponse ssresponse, final String body) throws WSException, IOException;

    public abstract void handleError(final WebSocketResponse ssresponse, final short statusCode, final String message);
}
