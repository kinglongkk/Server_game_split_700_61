package core.network.client2game.handler.dzpk;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.dzpk.c2s.iclass.CDZPK_CreateRoom;
import business.player.Player;
import business.player.feature.PlayerRoom;
import cenum.PrizeType;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.dzpk.DZPKAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

/**
 * 创建房间
 *
 * @author Administrator
 */
public class CDZPKCreateRoom extends PlayerHandler {

    @SuppressWarnings("rawtypes")
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
        SData_Result resule = player.getFeature(PlayerRoom.class).createRoomAndConsumeCard(configure);
        if (ErrorCode.Success.equals(resule.getCode())) {
            request.response(resule.getData());
        } else {
            request.error(resule.getCode(), resule.getMsg());
        }
    }
}		
