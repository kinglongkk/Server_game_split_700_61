package core.network.client2game.handler.base;

import java.io.IOException;

import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import core.network.client2game.handler.BaseHandler;

public class C1003HeartBeat extends BaseHandler {

    @Override
    public void handle(WebSocketRequest request, String message) throws IOException {
        request.response();
    }
}
