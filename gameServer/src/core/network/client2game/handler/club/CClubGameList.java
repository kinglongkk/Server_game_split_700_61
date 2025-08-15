package core.network.client2game.handler.club;

import business.global.club.Club;
import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.global.config.GameListConfigMgr;
import business.player.Player;
import business.player.PlayerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.cclass.club.Club_define.Club_MINISTER;
import jsproto.c2s.cclass.club.Club_define.Club_Player_Status;
import jsproto.c2s.iclass.club.CClub_BanGame;

import java.io.IOException;

/**
 * 亲友圈游戏列表
 * @author zaf
 *
 */
public class CClubGameList extends PlayerHandler {
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		final CClub_BanGame req = new Gson().fromJson(message, CClub_BanGame.class);
		// 获取亲友圈信息。
		Club club = ClubMgr.getInstance().getClubListMgr().findClub(req.clubId);
		if(null == club) {
			request.error(ErrorCode.NotAllow,"null == club clubId:{}",req.clubId);
			return;
		}
		request.response(GameListConfigMgr.getInstance().findGameIdList(club.getClubListBO().getCityId(),0L));

	}
}
