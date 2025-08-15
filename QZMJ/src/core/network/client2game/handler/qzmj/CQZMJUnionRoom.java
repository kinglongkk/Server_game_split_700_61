package core.network.client2game.handler.qzmj;

import java.io.IOException;

import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.feature.PlayerUnionRoom;
import business.qzmj.c2s.iclass.CQZMJ_CreateRoom;
import cenum.PrizeType;
import core.network.client2game.handler.PlayerHandler;
import core.server.qzmj.QZMJAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

/**
 * 亲友圈房间
 * 
 * @author Administrator
 *
 */
public class CQZMJUnionRoom extends PlayerHandler {

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
		player.getFeature(PlayerUnionRoom.class).createNoneUnionRoom(request,configure);
	}
}
