package core.network.client2game.handler.pdk;

import business.global.pk.pdk.PDKRoom;
import business.global.pk.pdk.PDKRoomSet;
import business.global.room.RoomMgr;
import business.player.Player;
import business.pdk.c2s.iclass.CPDK_RobClose;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/*
 * 抢关门
 * */

public class CPDKRobClose extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CPDK_RobClose clientPack = new Gson().fromJson(message, CPDK_RobClose.class);
    	
		PDKRoom room = (PDKRoom) RoomMgr.getInstance().getRoom(clientPack.roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "CPDKRobClose not find room:"+clientPack.roomID);
    		return;
    	}
    	PDKRoomSet set =  (PDKRoomSet) room.getCurSet();
    	if(null == set){
    		request.error(ErrorCode.NotAllow, "CPDKRobClose not set room:"+clientPack.roomID);
    		return;
    	}
    	set.onRobClose(request,  clientPack);		
	}
}
