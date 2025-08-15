package core.network.client2game.handler.nn;

import java.io.IOException;

import business.global.mj.set.MJOpCard;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.room.RoomMgr;
import business.global.room.mj.MahjongRoom;
import business.player.Player;
import cenum.mj.OpType;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.mj.CMJ_OpCard;

/**
 * 打牌
 * 
 * @author Huaxing
 *
 */
public class CNNOpCard extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {
		final CMJ_OpCard req = new Gson().fromJson(message, CMJ_OpCard.class);
		long roomID = req.roomID;
		OpType opType = OpType.valueOf(req.opType);

		MahjongRoom room = (MahjongRoom) RoomMgr.getInstance().getRoom(roomID);
		if (null == room) {
			request.error(ErrorCode.NotAllow, "CNNOpCard not find room:" + roomID);
			return;
		}
		SData_Result result = room.opCard(request, player.getId(), req.setID, req.roundID, opType, MJOpCard.OpCard(req.cardID));
    	if (!ErrorCode.Success.equals(result.getCode())) {
    		request.error(result.getCode(),result.getMsg());
    	}
	}
}
