package core.network.client2game.handler.club;

import java.io.IOException;
import java.util.List;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.club.ClubMgr;
import business.player.Player;
import business.player.feature.PlayerFamily;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.FamilyItem;
import jsproto.c2s.iclass.club.CClub_Create;

/**
 * 创建亲友圈
 * 
 * @author zaf
 *
 */
public class CClubCreate extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {

		final CClub_Create req = new Gson().fromJson(message, CClub_Create.class);
		SData_Result result = ClubMgr.getInstance().getClubListMgr().onClubCreate(req, player,req.getCityId());
		if (!ErrorCode.Success.equals(result.getCode())) {
			request.error(result.getCode(), result.getMsg());
			return;
		} else {
			request.response(result.getData());
		}
	}

}
