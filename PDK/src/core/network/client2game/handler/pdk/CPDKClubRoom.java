package core.network.client2game.handler.pdk;

import business.player.Player;
import business.player.feature.PlayerClubRoom;
import business.pdk.c2s.cclass.PDK_define;
import business.pdk.c2s.iclass.CPDK_CreateRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.server.pdk.PDKAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 亲友圈房间
 * 
 * @author Administrator
 *
 */
public class CPDKClubRoom extends PlayerHandler {

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
		player.getFeature(PlayerClubRoom.class).createNoneClubRoom(request,configure);
	}
}
