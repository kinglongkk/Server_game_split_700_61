package core.network.client2game.handler.union;

import business.global.union.UnionMgr;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.union.CUnion_AlivePointChange;
import jsproto.c2s.iclass.union.CUnion_SportsPointWarningChange;

import java.io.IOException;

/**
 *查询推广员身上的分成信息
 */
public class CUnionAlivePointInfo extends PlayerHandler {
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        CUnion_AlivePointChange sportsPointWarningChange=new Gson().fromJson(message, CUnion_AlivePointChange.class);
        SData_Result result = UnionMgr.getInstance().getUnionMemberMgr().getAlivePointInfo(sportsPointWarningChange,player.getPid());
        if(ErrorCode.Success.equals(result.getCode())) {
            request.response(result.getData());
        } else {
            request.error(result.getCode(),result.getMsg());
        }
    }
}
