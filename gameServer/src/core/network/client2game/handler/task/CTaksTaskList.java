package core.network.client2game.handler.task;

import java.io.IOException;

import business.player.feature.PlayerTask;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.player.Player;
import core.network.client2game.handler.PlayerHandler;

/**
 * 任务列表
 * @author Huaxing
 *
 */
public class CTaksTaskList extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws IOException {
		// 获取任务列表
		player.getFeature(PlayerTask.class).taskList(request);
	}
}
