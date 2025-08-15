package core.network.client2game.handler.sss;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import business.sss.c2s.iclass.CSSS_RoomRecord;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/**
 * 房间记录
 * @author Huaxing
 *
 */
public class CSSSRoomRecord extends PlayerHandler {
	

    @SuppressWarnings("rawtypes")
	@Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	final CSSS_RoomRecord req = new Gson().fromJson(message, CSSS_RoomRecord.class);
    	long roomID = req.roomID;

		AbsBaseRoom room = RoomMgr.getInstance().getRoom(roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "SSSSStartGame not find room:"+roomID);
    		return;
    	}

    	request.response(room.getRecord());
    }
}
