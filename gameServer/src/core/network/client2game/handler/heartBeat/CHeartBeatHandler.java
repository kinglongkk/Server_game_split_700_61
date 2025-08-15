package core.network.client2game.handler.heartBeat;

import java.io.IOException;
import java.util.Objects;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import business.player.Player;
import core.network.client2game.ClientSession;
import core.network.client2game.handler.BaseHandler;
import core.network.client2game.handler.PlayerHandler;


/**
 * 修改心跳，心跳不加锁
 */
public class CHeartBeatHandler extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		if (player.getLastTime() > 0L) {
			player.setLastTime(0L);
		}
		request.response();
	}
}
