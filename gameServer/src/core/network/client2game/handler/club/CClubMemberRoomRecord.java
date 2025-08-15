package core.network.client2game.handler.club;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.feature.PlayerRecord;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.CPlayer_RoomRecord;

/**
 * 玩家房间战绩
 *
 * @author Huaxing
 */
public class CClubMemberRoomRecord extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        final CPlayer_RoomRecord req = new Gson().fromJson(message, CPlayer_RoomRecord.class);
        SData_Result<?> result = player.getFeature(PlayerRecord.class).playerRoomRecord(req.pageNum, req.clubId, req.getType, req.sort);
        if (!ErrorCode.Success.equals(result.getCode())) {
            request.error(result.getCode(), "ErrorCode:{%s},Msg:{%s}", result.getCode(), result.getMsg());
            return;
        }
        request.response(result.getData());
    }

}
