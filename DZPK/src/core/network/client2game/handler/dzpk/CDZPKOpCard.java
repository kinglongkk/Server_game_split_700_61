package core.network.client2game.handler.dzpk;

import business.global.pk.PKOpCard;
import business.global.pk.PKRoom;
import business.global.room.RoomMgr;
import business.player.Player;
import business.dzpk.c2s.iclass.CDZPK_OpCard;
import cenum.PKOpType;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;

import java.io.IOException;
import java.util.Objects;

/**
 * 打牌
 *
 * @author Huaxing
 */
public class CDZPKOpCard extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CDZPK_OpCard req = new Gson().fromJson(message, CDZPK_OpCard.class);
        long roomID = req.roomID;
        PKOpType opType = PKOpType.valueOf(req.opType);
        PKRoom room = (PKRoom) RoomMgr.getInstance().getRoom(roomID);
        if (Objects.isNull(room)) {
            request.error(ErrorCode.NotAllow, "CDZPKOpCard not find room:" + roomID);
            return;
        }
        SData_Result result = room.opCard(request, player.getId(), req.setID, req.roundID, opType, PKOpCard.OpCard(req.cardType));
        if (!ErrorCode.Success.equals(result.getCode())) {
            request.error(result.getCode(), result.getMsg());
        }
    }
}		
