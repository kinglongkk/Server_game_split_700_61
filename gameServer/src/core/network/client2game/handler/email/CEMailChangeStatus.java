package core.network.client2game.handler.email;

import java.io.IOException;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.player.Player;
import core.network.client2game.handler.PlayerHandler;

/**
 * 阅读邮件
 * @author zaf
 *
 */
public class CEMailChangeStatus extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
		throws WSException, IOException {

		request.response();
	}

}
