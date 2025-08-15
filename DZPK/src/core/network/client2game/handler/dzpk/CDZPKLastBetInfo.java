package core.network.client2game.handler.dzpk;

import business.dzpk.c2s.iclass.CDZPK_LastBetInfo;
import business.global.pk.dzpk.DZPKRoom;
import business.global.room.RoomMgr;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;

import java.io.IOException;

/**
 * 进入房间
 *
 * @author Administrator
 */
public class CDZPKLastBetInfo extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CDZPK_LastBetInfo req = new Gson().fromJson(message, CDZPK_LastBetInfo.class);
        DZPKRoom room = (DZPKRoom) RoomMgr.getInstance().getRoom(req.getRoomID());
        if (null == room) {
            request.error(ErrorCode.NotAllow, "CDZPK_LastBetInfo not find room:" + req.getRoomID());
            return;
        }
        SData_Result result = room.getLastSetInfo(player.getPid());
        if (ErrorCode.Success.equals(result.getCode())) {
            request.response();
        } else {
            request.error(result.getCode(), result.getMsg());
        }
    }

}		
