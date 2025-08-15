package core.network.client2game.handler.base;

import java.io.IOException;

import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.player.Player;
import core.network.client2game.handler.PlayerHandler;

public class C2226InitNotice extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	
//    	request.response(NoticeMgr.getInstance().getNoticeInfoList());
    }
}