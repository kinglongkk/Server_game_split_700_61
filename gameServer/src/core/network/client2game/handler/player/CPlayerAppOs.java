package core.network.client2game.handler.player;

import java.io.IOException;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.CPlayer_AppOs;

public class CPlayerAppOs extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CPlayer_AppOs req = new Gson().fromJson(message, CPlayer_AppOs.class);
		player.getPlayerBO().saveOs(req.type);
		request.response();
	}

}
