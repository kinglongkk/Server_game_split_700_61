package core.network.client2game.handler.nn;

import business.nn.c2s.iclass.CNN_CreateRoom;
import business.player.Player;
import business.player.feature.PlayerClubRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.server.nn.NNAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

import java.io.IOException;

/**
 * 亲友圈房间
 *
 * @author Administrator
 */
public class CNNClubRoom extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message)
            throws IOException {

        final CNN_CreateRoom clientPack = new Gson().fromJson(message,
                CNN_CreateRoom.class);

        // 公共房间配置
        BaseRoomConfigure<CNN_CreateRoom> configure = new BaseRoomConfigure<CNN_CreateRoom>(
                PrizeType.RoomCard,
                NNAPP.GameType(),
                clientPack.clone());
        player.getFeature(PlayerClubRoom.class).createNoneClubRoom(request, configure);
    }
}
