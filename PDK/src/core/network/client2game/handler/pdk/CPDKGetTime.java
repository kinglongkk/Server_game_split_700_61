package core.network.client2game.handler.pdk;

import business.global.pk.pdk.PDKRoom;
import business.global.pk.pdk.PDKRoomSet;
import business.global.room.RoomMgr;
import business.pdk.c2s.iclass.CPDK_AddDouble;
import business.pdk.c2s.iclass.SPDK_GetTime;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

/**
 * @author zhujianming
 * @date 2022-04-22 10:09
 */
public class CPDKGetTime extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        final CPDK_AddDouble clientPack = new Gson().fromJson(message, CPDK_AddDouble.class);

        PDKRoom room = (PDKRoom) RoomMgr.getInstance().getRoom(clientPack.roomID);
        if (null == room){
            request.error(ErrorCode.NotAllow, "CPDKGetTime not find room:"+clientPack.roomID);
            return;
        }
        PDKRoomSet set =  (PDKRoomSet) room.getCurSet();
        if(null == set){
            request.error(ErrorCode.NotAllow, "CPDKGetTime not set room:"+clientPack.roomID);
            return;
        }

        SPDK_GetTime make = SPDK_GetTime.make(room.getRoomID(), player.getPid(), clientPack.pos);
        make.secTotal = set.getTime1(clientPack.pos);
        request.response(make);
    }
}
