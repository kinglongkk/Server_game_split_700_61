package core.network.client2game.handler.game;

import java.io.IOException;

import jsproto.c2s.iclass.SPlayer_Address;
import business.player.Player;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import core.network.client2game.handler.PlayerHandler;

/**
 * 用户IP 地址
 * @author 
 *
 */
public class CPlayerAddress extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		player.pushProto(SPlayer_Address.make(player.getPid(), player.getIp()));
		request.response();
	}

}
