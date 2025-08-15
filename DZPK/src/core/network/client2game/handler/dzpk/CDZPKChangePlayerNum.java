package core.network.client2game.handler.dzpk;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.room.CBase_ChangePlayerNum;

import java.io.IOException;

/**
 * 发起切换人数
 *
 * @author Huaxing
 */
public class CDZPKChangePlayerNum extends PlayerHandler {


    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {

        final CBase_ChangePlayerNum req = new Gson().fromJson(message, CBase_ChangePlayerNum.class);
        AbsBaseRoom room = RoomMgr.getInstance().getRoom(req.roomID);
        // 检查房间是否存在	
        if (null == room) {
            request.error(ErrorCode.NotFind_Room, "CDZPKChangePlayerNum null == room");
            return;
        }
        SData_Result result = room.getOpChangePlayerRoom().changePlayerNum(player.getPid());
        if (ErrorCode.Success.equals(result.getCode())) {
            request.response();
        } else {
            request.error(result.getCode(), result.getMsg());
        }
    }
}		
