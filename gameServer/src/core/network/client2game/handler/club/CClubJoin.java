package core.network.client2game.handler.club;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.club.ClubMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.club.CClub_Join;

/**
 * 申请加入亲友圈
 * @author zaf
 *
 */
public class CClubJoin extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
    	final CClub_Join req = new Gson().fromJson(message, CClub_Join.class);
    	
    	SData_Result result = ClubMgr.getInstance().onJoinClub(player, req);
    	if(ErrorCode.Success.equals(result.getCode())) {
    		request.response(result.getCustom());
    	} else {
    		request.error(result.getCode(), result.getMsg());
    	}
	}

}
