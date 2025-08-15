package core.network.client2game.handler.dzpk;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.room.CBase_KickRoom;

/**
 * 踢出房间
 *
 * @author Administrator
 */
public class CDZPKKickRoom extends PlayerHandler {


    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CBase_KickRoom req = new Gson().fromJson(message, CBase_KickRoom.class);
        long roomID = req.roomID;
        int posIndex = req.posIndex;
        AbsBaseRoom room = RoomMgr.getInstance().getRoom(roomID);
        if (null == room) {
            request.error(ErrorCode.NotAllow, "CDZPKKickRoom not find room:" + roomID);
            return;
        }
        SData_Result result = room.kickOut(player.getPid(), posIndex);
        if (ErrorCode.Success.equals(result.getCode())) {
            request.response();
        } else {
            request.error(result.getCode(), result.getMsg());
        }
    }
}		
