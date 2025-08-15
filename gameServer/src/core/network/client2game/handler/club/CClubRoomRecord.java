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
import jsproto.c2s.iclass.club.CClub_RoomRecord;

/**
 * 俱乐部房间战绩
 * @author Huaxing
 *
 */
public class CClubRoomRecord extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		final CClub_RoomRecord req = new Gson().fromJson(message, CClub_RoomRecord.class);
		SData_Result<?> result = player.getFeature(PlayerRecord.class).playerRoomRecord(req.clubId, req.type, req.pageNum);
		if (!ErrorCode.Success.equals(result.getCode())) {
			request.error(result.getCode(),"ErrorCode:{%s},ClubID:{%d}",result.getCode(),req.clubId);
			return;
		}
		request.response(result.getData());
	}

}
