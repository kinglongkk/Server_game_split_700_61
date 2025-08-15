package core.network.client2game.handler.room;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.config.GameListConfigMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.room.CBase_GameIdList;

public class CBaseGameIdList extends PlayerHandler {
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CBase_GameIdList req = new Gson().fromJson(message, CBase_GameIdList.class);
		// 设置请求的城市ID
		SData_Result result = player.saveCityId(req.getSelectCityId());
		if (ErrorCode.Success.equals(result.getCode())) {
			request.response(GameListConfigMgr.getInstance().findGameIdList(req.getSelectCityId(),0L));
		} else {
			request.error(result.getCode(),result.getMsg());
		}
	}
}