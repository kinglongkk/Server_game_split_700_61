package core.network.client2game.handler.dzpk;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.dzpk.c2s.iclass.CDZPK_CreateRoom;
import business.player.Player;
import business.player.feature.PlayerGoldRoom;
import cenum.PrizeType;
import core.config.refdata.RefDataMgr;
import core.config.refdata.ref.RefPractice;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.dzpk.DZPKAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.cclass.room.RobotRoomConfig;
import jsproto.c2s.iclass.room.CBase_GoldRoom;

/**
 * 创建房间
 *
 * @author Administrator
 */
public class CDZPKGoldRoom extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {

        final CBase_GoldRoom clientPack = new Gson().fromJson(message, CBase_GoldRoom.class);
        RefPractice data = RefDataMgr.get(RefPractice.class, clientPack.getPracticeId());
        if (data == null) {
            request.error(ErrorCode.NotAllow, "CDZPKGoldRoom do not find practiceId");
            return;
        }
        // 游戏配置	
        CDZPK_CreateRoom createClientPack = new CDZPK_CreateRoom();
        // 公共房间配置	
        BaseRoomConfigure<CDZPK_CreateRoom> configure = new BaseRoomConfigure<CDZPK_CreateRoom>(PrizeType.Gold,
                DZPKAPP.GameType(), createClientPack.clone(), new RobotRoomConfig(data.getBaseMark(), data.getMin(), data.getMax(), clientPack.getPracticeId()));
        SData_Result resule = player.getFeature(PlayerGoldRoom.class).createAndQuery(configure);
        if (ErrorCode.Success.equals(resule.getCode())) {
            request.response(resule.getData());
        } else {
            request.error(resule.getCode(), resule.getMsg());
        }
    }
}		
