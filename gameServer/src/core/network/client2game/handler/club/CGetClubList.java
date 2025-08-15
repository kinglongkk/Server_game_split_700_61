package core.network.client2game.handler.club;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.global.club.ClubMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;

/**
 * 获取俱乐部列表
 * @authot zaf
 * */

public class CGetClubList extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		SData_Result result = ClubMgr.getInstance().onGetClubListByPlayer(player);
		if (ErrorCode.Success.equals(result.getCode())) {
			request.response(result.getData());
		} else {
			request.error(result.getCode(),result.getMsg());
		}
	}

}
