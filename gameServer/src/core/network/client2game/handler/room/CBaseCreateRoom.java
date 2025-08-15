package core.network.client2game.handler.room;

import business.global.GM.MaintainGameMgr;
import business.global.config.GameListConfigMgr;
import business.global.sharegm.ShareNodeServerMgr;
import business.player.Player;
import business.rocketmq.bo.MqAbsRequestBo;
import business.rocketmq.constant.MqTopic;
import business.shareplayer.ShareNode;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.config.server.GameTypeMgr;
import core.network.client2game.ClientAcceptor;
import core.network.client2game.handler.BaseHandler;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.cclass.GameTypeUrl;
import jsproto.c2s.iclass.CGameType;

import java.io.IOException;
import java.util.Objects;

public class CBaseCreateRoom extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CGameType clientPack = new Gson().fromJson(message, CGameType.class);
        GameType gameType = GameTypeMgr.getInstance().gameType(clientPack.getGameType());
        long requestId = System.nanoTime();
        CommLogD.info("创建房间开始[{}],请求标识[{}]", gameType.getName(), requestId);
        if (Objects.nonNull(gameType)) {
            if (Config.isShare()) {
                if (GameListConfigMgr.getInstance().checkIsLiveByGameType(gameType.getId())) {
                    //检查游戏是否在维护中
                    SData_Result result = MaintainGameMgr.getInstance().checkMaintainGame(gameType.getId(), player);
                    if (!ErrorCode.Success.equals(result.getCode())) {
                        request.error(ErrorCode.Game_Maintain, MaintainGameMgr.getInstance().getMaintainGameContent(gameType.getId()));
                        return;
                    }
                    GameTypeUrl gameTypeUrl = GameListConfigMgr.getInstance().getByGameType(gameType.getId());
                    ShareNode shareNode = new ShareNode("", gameTypeUrl.getWebSocketUrl(), gameTypeUrl.getGameServerIP(), gameTypeUrl.getGameServerPort());
                    MqAbsRequestBo mqAbsRequestBo = new MqAbsRequestBo(player.getPid(), gameType.getName(), gameType.getId(), message, request.getHeader().event, shareNode);
                    mqAbsRequestBo.setRequestId(requestId);
                    mqAbsRequestBo.setShareNodeFrom(ShareNodeServerMgr.getInstance().getThisNode());
                    //推送到MQ
                    MqProducerMgr.get().send(MqTopic.BASE_CREATE_ROOM + gameType.getId(), mqAbsRequestBo);
                } else {
                    request.error(ErrorCode.Server_Maintain, String.valueOf(System.currentTimeMillis() / 1000 + 300));
                }
            } else {
                String gameTypeName = gameType.getName().toLowerCase();
                BaseHandler handler = (BaseHandler) ClientAcceptor.getInstance().getHandle(gameTypeName + ".c" + gameTypeName + "createroom");
                handler.handle(request, message);
                return;
            }
        }
        request.response();
    }

}
