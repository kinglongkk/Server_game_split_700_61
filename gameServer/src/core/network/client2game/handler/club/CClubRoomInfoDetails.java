package core.network.client2game.handler.club;

import business.global.club.ClubMgr;
import business.global.union.UnionMgr;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.club.CClub_RoomInfoDetails;
import jsproto.c2s.iclass.union.CUnion_RoomInfoDetails;

import java.io.IOException;

/**
 * 获取指定房间详情
 * @author zaf
 *
 */
public class CClubRoomInfoDetails extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
    	final CClub_RoomInfoDetails req = new Gson().fromJson(message, CClub_RoomInfoDetails.class);
    	SData_Result result = ClubMgr.getInstance().getRoomInfoDetails(req.getClubId(),player.getPid(),req.getRoomKey());
    	if(ErrorCode.Success.equals(result.getCode())) {
    		request.response(result.getData());
    	} else {
    		request.error(result.getCode(), result.getMsg());
    	}
	}

}
