package core.network.client2game.handler.sss;

import business.player.Player;
import business.player.feature.PlayerUnionRoom;
import business.sss.c2s.iclass.CSSS_CreateRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.server.sss.SSSAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

import java.io.IOException;

public class CSSSUnionRoom extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message)
            throws IOException {

        final CSSS_CreateRoom clientPack = new Gson().fromJson(message,
                CSSS_CreateRoom.class);

        // 公共房间配置
        BaseRoomConfigure<CSSS_CreateRoom> configure = new BaseRoomConfigure<CSSS_CreateRoom>(
                PrizeType.RoomCard,
                SSSAPP.GameType(),
                clientPack.clone());
        player.getFeature(PlayerUnionRoom.class).createNoneUnionRoom(request,configure);
    }
}

