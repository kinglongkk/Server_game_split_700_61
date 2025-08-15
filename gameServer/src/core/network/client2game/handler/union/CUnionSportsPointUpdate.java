package core.network.client2game.handler.union;

import business.global.union.UnionMgr;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.union.CUnion_PostTypeUpdate;
import jsproto.c2s.iclass.union.CUnion_SportsPointUpdate;

import java.io.IOException;

/**
 * 执行竞技点修改
 */
public class CUnionSportsPointUpdate extends PlayerHandler {
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        final CUnion_SportsPointUpdate req = new Gson().fromJson(message, CUnion_SportsPointUpdate.class);
        SData_Result result = UnionMgr.getInstance().getUnionMemberMgr().execSportsPointUpdate(req,player.getPid());
        if(ErrorCode.Success.equals(result.getCode())) {
            request.response(result.getData());
        } else {
            request.error(result.getCode(), result.getMsg());
        }
    }
}
