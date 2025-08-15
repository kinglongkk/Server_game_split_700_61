package core.network.client2game.handler.nn;

import java.io.IOException;

import business.global.pk.nn.NNRoom;
import business.global.pk.nn.NNRoomSet;
import business.nn.c2s.iclass.CNN_CheckCard;
import business.global.room.RoomMgr;
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
public class CNNCheckCard extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message)
            throws WSException, IOException {
        final CNN_CheckCard req = new Gson().fromJson(message, CNN_CheckCard.class);
        long roomID = req.roomID;

        NNRoom room = (NNRoom) RoomMgr.getInstance().getRoom(roomID);
        if (null == room) {
            request.error(ErrorCode.NotAllow, "CNNCheckCard not find room:" + roomID);
            return;
        }
        NNRoomSet set = (NNRoomSet) room.getCurSet();
        if (null == set) {
            request.error(ErrorCode.NotAllow, "CNNCheckCard not set room:" + roomID);
            return;
        }
        set.onCheckCard(request, req);
    }

}
