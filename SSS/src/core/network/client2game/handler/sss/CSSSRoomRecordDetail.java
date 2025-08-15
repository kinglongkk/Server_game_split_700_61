package core.network.client2game.handler.sss;

import business.player.Player;
import business.sss.c2s.iclass.CSSS_RoomRecordDetail;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
//import jsproto.c2s.iclass.sss.CSSS_RoomRecordDetail;

import java.io.IOException;

/**
 * 房间记录详情
 * @author Huaxing
 *
 */
public class CSSSRoomRecordDetail extends PlayerHandler {
	

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	final CSSS_RoomRecordDetail req = new Gson().fromJson(message, CSSS_RoomRecordDetail.class);
    	long roomID = req.roomID;
    	
//    	RoomRecord record = RoomRecordMgr.getInstance().getRoom(roomID);
//    	if (null == record){
//    		request.error(ErrorCode.NotAllow, "not find roomID:" + roomID);
//    		return;
//    	}
//
    	request.response();
    }
}
