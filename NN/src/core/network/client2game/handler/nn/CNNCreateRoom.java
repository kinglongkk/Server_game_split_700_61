package core.network.client2game.handler.nn;

import java.io.IOException;

import business.nn.c2s.iclass.CNN_CreateRoom;
import core.network.http.proto.SData_Result;
import core.server.nn.NNAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import business.player.Player;
import business.player.feature.PlayerRoom;
import cenum.PrizeType;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import core.network.client2game.handler.PlayerHandler;

public class CNNCreateRoom extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        // TODO 自动生成的方法存根
        final CNN_CreateRoom clientPack = new Gson().fromJson(message,
                CNN_CreateRoom.class);
        // 公共房间配置
        BaseRoomConfigure<CNN_CreateRoom> configure = new BaseRoomConfigure<CNN_CreateRoom>(
                PrizeType.RoomCard,
                NNAPP.GameType(),
                clientPack.clone());
        SData_Result resule = player.getFeature(PlayerRoom.class).createRoomAndConsumeCard(configure);
        if (ErrorCode.Success.equals(resule.getCode())) {
            request.response(resule.getData());
        } else {
            request.error(resule.getCode(), resule.getMsg());
        }
    }

}
