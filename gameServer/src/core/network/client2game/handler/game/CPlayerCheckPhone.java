package core.network.client2game.handler.game;

import java.io.IOException;

import com.ddm.server.common.utils.StringUtil;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.player.Player;
import core.network.client2game.handler.PlayerHandler;

/**
 * 检查手机号手机
 * @author Huaxing
 *
 */
public class CPlayerCheckPhone extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		if (player.getPlayerBO().getPhone() <= 0L) {
			request.response("0");
		}
		request.response(StringUtil.getPhone(String.valueOf(player.getPlayerBO().getPhone())));
	}

}
