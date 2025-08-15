package core.network.client2game.handler.nn;

import business.global.pk.nn.NNRoom;
import business.global.room.RoomMgr;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.room.CBase_RoomRecord;

import java.io.IOException;

/**
 * 房间记录
 * @author Administrator
 *
 */
public class CNNRoomRecord extends PlayerHandler {
	

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	final CBase_RoomRecord req = new Gson().fromJson(message, CBase_RoomRecord.class);
    	
    	NNRoom room = (NNRoom) RoomMgr.getInstance().getRoom(req.getRoomID());
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "CNNRoomRecord not find room:"+req.getRoomID());
    		return;
    	}

    	request.response(room.getRecord());
    }
}
