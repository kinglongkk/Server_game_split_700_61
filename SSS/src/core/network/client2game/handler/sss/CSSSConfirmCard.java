package core.network.client2game.handler.sss;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import business.sss.c2s.iclass.CSSS_Ranked;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/**
 * 玩家准备
 * @author Huaxing
 *
 */
public class CSSSConfirmCard extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
    	final CSSS_Ranked req = new Gson().fromJson(message, CSSS_Ranked.class);
    	long roomID = req.roomID;

    	if (req.pid != player.getPid()) {
    		request.error(ErrorCode.NotAllow, "CSSSConfirmCard not find pid error "+ req.pid);
    		return;
    	}
    	
    	int posIndex = req.posIdx;
    	if (posIndex < 0 || posIndex > 7){
    		request.error(ErrorCode.NotAllow, "posIndex:"+posIndex);
    		return;
    	}

		AbsBaseRoom room = RoomMgr.getInstance().getRoom(roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "SSSSReadyRoom not find room:"+roomID);
    		return;
    	}

    	room.playerReady(true, player.getPid());

		
	}

}
