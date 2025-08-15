package core.network.client2game.handler.union;

import business.global.club.Club;
import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.global.config.GameListConfigMgr;
import business.global.union.Union;
import business.global.union.UnionMgr;
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
import jsproto.c2s.iclass.union.CUnion_Base;

import java.io.IOException;

/**
 * 赛事游戏列表
 * @author zaf
 *
 */
public class CUnionGameList extends PlayerHandler {
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		final CUnion_Base req = new Gson().fromJson(message, CUnion_Base.class);
		// 获取亲友圈信息。
		Union union = UnionMgr.getInstance().getUnionListMgr().findUnion(req.getUnionId());
		if(null == union) {
			request.error(ErrorCode.NotAllow,"null == union unionId:{}",req.getUnionId());
			return;
		}
		request.response(GameListConfigMgr.getInstance().findGameIdList(union.getUnionBO().getCityId(),union.getUnionBO().getUnionSign()));
	}
}
