package com.ddm.server.websocket.handler.requset;

import java.io.IOException;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.IBaseHandler;

public abstract class RequestHandler extends IBaseHandler {

    public RequestHandler() {
        super();
    }

    public RequestHandler(short opCode, String opName) {
        super(opCode, opName);
    }

    public abstract void handleMessage(final WebSocketRequest request, final String data) throws WSException, IOException;
}
