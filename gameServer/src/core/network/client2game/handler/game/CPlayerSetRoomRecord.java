package core.network.client2game.handler.game;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.feature.PlayerRecord;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.CPlayer_SetRoomRecord;

/**
 * 获取房间每局信息
 * @author Huaxing
 *
 */
public class CPlayerSetRoomRecord extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		final CPlayer_SetRoomRecord req = new Gson().fromJson(message, CPlayer_SetRoomRecord.class);
		SData_Result<?> result = player.getFeature(PlayerRecord.class).playerSetRoomRecord(req.roomID);
		if (!ErrorCode.Success.equals(result.getCode())) {
			request.error(result.getCode(), "ErrorCode:{%s},Msg:{%s}", result.getCode(),result.getMsg());
			return;
		}
		request.response(result.getData());
	}
}
