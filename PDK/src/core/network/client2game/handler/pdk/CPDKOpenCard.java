package core.network.client2game.handler.pdk;

import business.global.pk.pdk.PDKRoom;
import business.global.pk.pdk.PDKRoomSet;
import business.global.room.RoomMgr;
import business.player.Player;
import business.pdk.c2s.iclass.CPDK_OpenCard;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/**
 * 明牌
 * */

public class CPDKOpenCard extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CPDK_OpenCard clientPack = new Gson().fromJson(message, CPDK_OpenCard.class);
    	
		PDKRoom room = (PDKRoom) RoomMgr.getInstance().getRoom(clientPack.roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "CPDKOpenCard not find room:"+clientPack.roomID);
    		return;
    	}
    	PDKRoomSet set =  (PDKRoomSet) room.getCurSet();
    	if(null == set){
    		request.error(ErrorCode.NotAllow, "CPDKOpenCard not set room:"+clientPack.roomID);
    		return;
    	}
		request.response();
//    	set.onOpenCard(request, clientPack);
	}
}
