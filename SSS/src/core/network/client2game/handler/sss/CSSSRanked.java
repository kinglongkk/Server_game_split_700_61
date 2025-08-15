package core.network.client2game.handler.sss;

import business.global.pk.sss.SSSRoom;
import business.global.room.RoomMgr;
import business.player.Player;
import business.sss.c2s.iclass.CSSS_Ranked;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/**
 * 确认牌的排列
 * @author Huaxing
 *
 */
public class CSSSRanked extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
    	final CSSS_Ranked req = new Gson().fromJson(message, CSSS_Ranked.class);
    	long roomID = req.roomID;
    	boolean isSpecial = req.isSpecial;
    	if (req.pid != player.getPid()) {
    		request.error(ErrorCode.NotAllow, "CSSSRanked not find pid error "+ req.pid);
    		return;
    	}
    	
    	int posIdx = req.posIdx;
    	if (posIdx < 0 || posIdx > 7){
    		request.error(ErrorCode.NotAllow, "posIndex:"+posIdx);
    		return;
    	}
    	
    	
    	SSSRoom<?> room = (SSSRoom<?>) RoomMgr.getInstance().getRoom(roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "CSSSRanked not find room:"+roomID);
    		return;
    	}
    	if (room.getRoomPosMgr().getPosByPosID(posIdx).isTrusteeship()) {
    		request.response();
    		return;
    	}
    	room.playerCardReady(request, true, player.getPid(), posIdx,req,isSpecial);

		
	}

}
