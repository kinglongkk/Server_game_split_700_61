package core.network.client2game.handler.sss;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.room.CBase_DissolveRoom;

import java.io.IOException;

public class CSSSEndRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {

		final CBase_DissolveRoom req = new Gson().fromJson(message, CBase_DissolveRoom.class);
		long roomID = req.getRoomID();

		AbsBaseRoom room = RoomMgr.getInstance().getRoom(roomID);
		if (null == room) {
			request.error(ErrorCode.NotAllow, "SSSSEndRoom not find room:" + roomID);
			return;
		}
		
		room.endRoom();
	}
}