package core.network.client2game.handler.dzpk;

import business.dzpk.c2s.iclass.CDZPK_JiFen;
import business.global.pk.dzpk.DZPKRoom;
import business.global.pk.dzpk.DZPKRoomSet;
import business.global.room.RoomMgr;
import business.global.room.base.AbsRoomPos;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;

import java.io.IOException;

public class CDZPKJiFen extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        final CDZPK_JiFen clientPack = new Gson().fromJson(message, CDZPK_JiFen.class);

        DZPKRoom room = (DZPKRoom) RoomMgr.getInstance().getRoom(clientPack.roomID);
        if (null == room) {
            request.error(ErrorCode.NotAllow, "CDZPKDaiFen not find room:" + clientPack.roomID);
            return;
        }
        DZPKRoomSet set = (DZPKRoomSet) room.getCurSet();
        if (null == set) {
            request.error(ErrorCode.NotAllow, "CDZPKDaiFen not set room:" + clientPack.roomID);
            return;
        }
        AbsRoomPos posByPid = room.getRoomPosMgr().getPosByPid(player.getPid());
        set.doInitJifenPai(request, posByPid.getPosID(), clientPack.point);
        request.response(ErrorCode.Success);
    }
}		
