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
public class CClubGroupingRemove extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		final CClub_GroupingPid req = new Gson().fromJson(message, CClub_GroupingPid.class);
		// 检查当前操作者是否管理员、创建者。
		if (!ClubMgr.getInstance().getClubMemberMgr().isMinister(req.clubId, player.getPid())) {
			request.error(ErrorCode.NotAllow,"not club admin ClubID:{%d}",req.clubId);
			return;
		}
		// 获取指定的亲友圈信息
		Club club= ClubMgr.getInstance().getClubListMgr().findClub(req.clubId);
		if (null == club) {
			request.error(ErrorCode.NotAllow,"null == club ClubID:{%d}",req.clubId);
			return;
		}		
		// 移除分组
		ClubGroupingInfo cInfo = club.removeClubGrouping(req.groupingId);
		if (null == cInfo) {
			request.error(ErrorCode.NotAllow,"removeClubGrouping ClubID:{%d}",req.clubId);
			return;
		}
		request.response(cInfo);
	}
}
