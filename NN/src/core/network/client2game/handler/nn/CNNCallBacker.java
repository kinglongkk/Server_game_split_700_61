package core.network.client2game.handler.nn;

import java.io.IOException;

import business.global.pk.nn.NNRoom;
import business.global.pk.nn.NNRoomSet;
import business.global.room.RoomMgr;
import business.nn.c2s.iclass.CNN_CallBacker;
import business.player.Player;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import core.network.client2game.handler.PlayerHandler;

/**
 * 确认牌的顺序
 *
 * @author Huaxing
 */
public class CNNCallBacker extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message)
            throws WSException, IOException {
        final CNN_CallBacker req = new Gson().fromJson(message, CNN_CallBacker.class);
        long roomID = req.roomID;

        NNRoom room = (NNRoom) RoomMgr.getInstance().getRoom(roomID);
        if (null == room) {
            request.error(ErrorCode.NotAllow, "CNNCallBacker not find room:" + roomID);
            return;
        }
        NNRoomSet set = (NNRoomSet) room.getCurSet();
        if (null == set) {
            request.error(ErrorCode.NotAllow, "CNNCallBacker not set room:" + roomID);
            return;
        }
        set.onCallBacker(request, req);

    }

}
