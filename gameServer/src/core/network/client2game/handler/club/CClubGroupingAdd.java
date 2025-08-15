package core.network.client2game.handler.club;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.club.Club;
import business.global.club.ClubMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.club.ClubGroupingInfo;
import jsproto.c2s.iclass.club.CClub_GroupingPid;

/**
 * 亲友圈分组增加
 * @author zaf
 *
 */
public class CClubGroupingAdd extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		final CClub_GroupingPid req = new Gson().fromJson(message, CClub_GroupingPid.class);
		SData_Result result=ClubMgr.getInstance().getClubMemberMgr().checkClubGetMemberPromotionList(req.clubId,player.getPid());
		if (!ErrorCode.Success.equals(result.getCode())) {
			request.error(result.getCode(), result.getMsg());
			return;
		}
		// 获取指定的亲友圈信息
		Club club= ClubMgr.getInstance().getClubListMgr().findClub(req.clubId);
		if (null == club) {
			request.error(ErrorCode.NotAllow,"null == club ClubID:{%d}",req.clubId);
			return;
		}
		// 增加分组
		ClubGroupingInfo cInfo = club.addClubGrouping();
		if (null == cInfo) {
			request.error(ErrorCode.NotAllow,"addClubGrouping ClubID:{%d}",req.clubId);
			return;
		}
		request.response(cInfo);
	}
}
