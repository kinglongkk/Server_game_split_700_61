package core.network.client2game.handler.base;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import core.config.server.GameTypeMgr;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.iclass.C1110_UUID;

/**
 * 获取玩家UUID
 * @author Administrator
 *
 */
public class C1110UUID extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final C1110_UUID req = new Gson().fromJson(message, C1110_UUID.class);
		String gameName = req.gameName;
		if(StringUtils.isEmpty(gameName)) {
			// 游戏类型为空
			request.error(ErrorCode.NotAllow, "gameName error gameName:{%s}",gameName);
			return;
		}
		request.response(player.getuUID(gameName));
		player.uuidDisconnect();
	}
}
