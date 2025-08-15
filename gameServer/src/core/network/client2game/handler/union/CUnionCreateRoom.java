package core.network.client2game.handler.union;

import business.global.GM.MaintainGameMgr;
import business.global.config.GameListConfigMgr;
import business.player.Player;
import business.player.feature.PlayerUnionRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.config.server.GameTypeMgr;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.cclass.room.BaseCreateRoom;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.iclass.CGameType;

import java.io.IOException;

/**
 * @author : xushaojun
 * create at:  2020-08-14  10:00
 * @description: 赛事房间创建
 */
public class CUnionCreateRoom extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CGameType clientPack = new Gson().fromJson(message, CGameType.class);
        GameType gameType = GameTypeMgr.getInstance().gameType(clientPack.getGameType());
        if (gameType != null) {
            if(GameListConfigMgr.getInstance().checkIsLiveByGameType(gameType.getId())) {
                //检查游戏是否在维护中
                SData_Result result = MaintainGameMgr.getInstance().checkMaintainGame(gameType.getId(), player);
                if (!ErrorCode.Success.equals(result.getCode())) {
                    request.error(ErrorCode.Game_Maintain, MaintainGameMgr.getInstance().getMaintainGameContent(gameType.getId()));
                    return;
                }
                BaseCreateRoom createRoom = new Gson().fromJson(message, BaseCreateRoom.class);
                BaseRoomConfigure<BaseCreateRoom> configure = new BaseRoomConfigure<>(
                        PrizeType.RoomCard,
                        gameType,
                        createRoom,
                        message);
                player.getFeature(PlayerUnionRoom.class).createNoneUnionRoom(request, configure);
            } else {
                request.error(ErrorCode.Server_Maintain, String.valueOf(System.currentTimeMillis()/1000 + 300));
            }
        }
    }

}
