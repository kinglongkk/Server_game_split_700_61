package core.network.client2game.handler.pdk;

import java.io.IOException;

import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import business.player.feature.PlayerGoldRoom;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.S1101_GetRoomID;

public class CPDKGetRoomID extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {
		// 获取房间ID
		long roomID = player.getRoomInfo().getRoomId();
		// 练习场ID
		long practiceId = player.getFeature(PlayerGoldRoom.class).getPracticeId();
		if (roomID > 0L) {
			// 获取房间信息
			AbsBaseRoom room = RoomMgr.getInstance().getRoom(roomID);
			if (null == room) {
				roomID = 0L;
				// 强制清空用户的游戏状态
				player.onGMExitRoom();
			}
		}
		request.response(S1101_GetRoomID.make(roomID, null, practiceId));// 无账号情况下返回为空
	}


}
