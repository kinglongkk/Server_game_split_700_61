package core.network.client2game.handler.sss;

import business.global.pk.sss.SSSRoom;
import business.global.room.RoomMgr;
import business.player.Player;
import business.sss.c2s.iclass.CSSS_RoomBeiShu;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

public class CSSSRoomBeiShu extends PlayerHandler {
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {
		final CSSS_RoomBeiShu req = new Gson().fromJson(message, CSSS_RoomBeiShu.class);
		long roomID = req.roomID;
		int posIndex = req.posIndex;
		int beishu = req.beishu;
		if (posIndex < 0 || posIndex > 7) {
			request.error(ErrorCode.NotAllow, "posIndex:" + posIndex);
			return;
		}
		SSSRoom<?> room = (SSSRoom<?>)RoomMgr.getInstance().getRoom(roomID);
		if (null == room) {
			request.error(ErrorCode.NotAllow, "SSSSReadyRoom not find room:" + roomID);
			return;
		}
		if (beishu < 0 || beishu > 4) {
			request.error(ErrorCode.NotAllow, "beishu:" + beishu);
			return;
		}
		room.setBeishu(request, player.getPid(), posIndex,beishu);
	}
}
