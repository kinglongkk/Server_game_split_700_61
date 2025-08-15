package core.network.client2game.handler.qzmj;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.feature.PlayerRoom;
import business.qzmj.c2s.iclass.CQZMJ_CreateRoom;
import cenum.PrizeType;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.qzmj.QZMJAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

/**
 * 创建房间
 * 
 * @author Administrator
 *
 */
public class CQZMJCreateRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws IOException {

		final CQZMJ_CreateRoom clientPack = new Gson().fromJson(message,
				CQZMJ_CreateRoom.class);
		// 公共房间配置
		BaseRoomConfigure<CQZMJ_CreateRoom> configure = new BaseRoomConfigure<CQZMJ_CreateRoom>(
				PrizeType.RoomCard,
				QZMJAPP.GameType(),
				clientPack.clone());
		SData_Result resule = player.getFeature(PlayerRoom.class).createRoomAndConsumeCard(configure);
		if (ErrorCode.Success.equals(resule.getCode())) {
			request.response(resule.getData());
		} else {
			request.error(resule.getCode(),resule.getMsg());
		}
	}
}
