package core.network.client2game.handler.pdk;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.pdk.c2s.iclass.CPDK_OpCard;
import business.global.pk.pdk.PDKRoom;
import business.global.pk.pdk.PDKRoomSet;
import business.global.pk.pdk.PDKRoomSetSound;
import business.global.room.RoomMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;

/**
 * 操作牌
 * */

public class CPDKOpCard extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CPDK_OpCard clientPack = new Gson().fromJson(message, CPDK_OpCard.class);
    	
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
    	PDKRoomSetSound sound = set.getCurRound();
    	if(null == sound){
    		request.error(ErrorCode.NotAllow, "CPDKOpenCard not sound room:"+clientPack.roomID);
    		return;
    	}
    	sound.onOpCard(request,  clientPack,true);
	}
}
