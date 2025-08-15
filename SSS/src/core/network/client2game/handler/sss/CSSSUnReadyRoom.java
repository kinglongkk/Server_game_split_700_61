package core.network.client2game.handler.sss;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import business.sss.c2s.iclass.CSSS_UnReadyRoom;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/**
 * 没有准备的房间
 * @author Huaxing
 *
 */
public class CSSSUnReadyRoom extends PlayerHandler {


    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	final CSSS_UnReadyRoom req = new Gson().fromJson(message, CSSS_UnReadyRoom.class);
    	long roomID = req.roomID;
    	int posIndex = req.posIndex;

    	if (posIndex < 0 || posIndex > 7){
    		request.error(ErrorCode.NotAllow, "posIndex:"+posIndex);
    		return;
    	}
    	
    	AbsBaseRoom room = RoomMgr.getInstance().getRoom(roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "SSSSUnReadyRoom not find room:"+roomID);
    		return;
    	}
    	if (0 == posIndex){
    		request.error(ErrorCode.NotAllow, "SSSSUnReadyRoom you are ownner:");
    		return;
    	}
    	room.playerReady(false, player.getPid());
    }
}
