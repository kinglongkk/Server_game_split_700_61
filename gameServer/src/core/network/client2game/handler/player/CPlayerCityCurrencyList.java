package core.network.client2game.handler.player;

import business.player.Player;
import business.player.feature.PlayerCityCurrency;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/**
 * 获取玩家城市钻石列表
 */
public class CPlayerCityCurrencyList extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		request.response(player.getFeature(PlayerCityCurrency.class).getPlayerCityCurrencyList());
	}

}
