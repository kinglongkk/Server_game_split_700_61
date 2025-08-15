package core.network.client2game.handler.room;

import business.global.config.GameListConfigMgr;
import business.global.shareroom.ShareRoom;
import business.global.shareroom.ShareRoomMgr;
import business.player.Player;
import business.rocketmq.bo.MqAbsRequestBo;
import business.rocketmq.constant.MqTopic;
import business.shareplayer.ShareNode;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.config.server.GameTypeMgr;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.iclass.room.CBase_ExitRoom;

import java.io.IOException;

public class CBaseExitRoom extends PlayerHandler {
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        final CBase_ExitRoom req = new Gson().fromJson(message, CBase_ExitRoom.class);
        ShareRoom shareRoom = ShareRoomMgr.getInstance().getShareRoomByRoomId(req.getRoomID());
        if (shareRoom != null) {
            GameType gameType = GameTypeMgr.getInstance().gameType(shareRoom.getBaseRoomConfigure().getGameType().getId());
            ShareNode shareNode= GameListConfigMgr.getInstance().getShareNodeByRoom(shareRoom);
            MqAbsRequestBo mqAbsRequestBo = new MqAbsRequestBo(player.getPid(), gameType.getName(), gameType.getId(), message, request.getHeader().event, shareNode);
            //推送到MQ
            MqProducerMgr.get().send(MqTopic.BASE_EXIT_ROOM + gameType.getId(), mqAbsRequestBo);
        }
        request.response();
    }
}
