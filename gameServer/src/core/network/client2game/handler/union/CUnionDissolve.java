package core.network.client2game.handler.union;

import BaseCommon.CommLog;
import business.global.room.NormalRoomMgr;
import business.global.room.base.RoomImpl;
import business.global.union.UnionMember;
import business.global.union.UnionMgr;
import business.player.Player;
import cenum.RoomTypeEnum;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.club.Club_define;
import jsproto.c2s.cclass.union.UnionDefine;
import jsproto.c2s.iclass.union.CUnion_Dissolve;
import jsproto.c2s.iclass.union.CUnion_DissolveRoom;

import java.io.IOException;
import java.util.Objects;

/**
 * 解散联赛
 * @author Huaxing
 *
 */
public class CUnionDissolve extends PlayerHandler {


    @SuppressWarnings("rawtypes")
	@Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	final CUnion_Dissolve req = new Gson().fromJson(message, CUnion_Dissolve.class);
		SData_Result result = UnionMgr.getInstance().execUnionDissolve(req,player);
		if (ErrorCode.Success.equals(result.getCode())) {
			request.response();
		} else {
			request.error(result.getCode(),result.getMsg());
		}
    }
}
