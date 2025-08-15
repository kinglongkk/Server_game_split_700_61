package core.network.client2game.handler.pdk;

import business.player.Player;
import business.player.feature.PlayerRoom;
import business.pdk.c2s.iclass.CPDK_CreateRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.pdk.PDKAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

import java.io.IOException;

/**
 * 创建房间
 * 
 * @author Administrator
 *
 */
public class CPDKCreateRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws IOException {

		final CPDK_CreateRoom clientPack = new Gson().fromJson(message,
				CPDK_CreateRoom.class);
		// 公共房间配置
		BaseRoomConfigure<CPDK_CreateRoom> configure = new BaseRoomConfigure<CPDK_CreateRoom>(
				PrizeType.RoomCard,
				PDKAPP.GameType(),
				clientPack.clone());
		SData_Result resule = player.getFeature(PlayerRoom.class).createRoomAndConsumeCard(configure);
		if (ErrorCode.Success.equals(resule.getCode())) {
			request.response(resule.getData());
		} else {
			request.error(resule.getCode(),resule.getMsg());
		}
	}
}
