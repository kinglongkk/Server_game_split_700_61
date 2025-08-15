package core.network.client2game.handler.dzpk;

import business.player.Player;
import business.player.feature.PlayerUnionRoom;
import business.dzpk.c2s.iclass.CDZPK_CreateRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.server.dzpk.DZPKAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

import java.io.IOException;

/**
 * 亲友圈房间
 *
 * @author Administrator
 */
public class CDZPKUnionRoom extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message)
            throws IOException {

        final CDZPK_CreateRoom clientPack = new Gson().fromJson(message,
                CDZPK_CreateRoom.class);

        // 公共房间配置	
        BaseRoomConfigure<CDZPK_CreateRoom> configure = new BaseRoomConfigure<CDZPK_CreateRoom>(
                PrizeType.RoomCard,
                DZPKAPP.GameType(),
                clientPack.clone());
        player.getFeature(PlayerUnionRoom.class).createNoneUnionRoom(request, configure);
    }
}		
