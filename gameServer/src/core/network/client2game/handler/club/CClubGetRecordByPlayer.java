package core.network.client2game.handler.club;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.cclass.club.Club_define.Club_Player_Status;
import jsproto.c2s.iclass.club.CClub_GetRecordByPlayer;

/**
 * 加入俱乐部
 * @author zaf
 *
 */
public class CClubGetRecordByPlayer extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
    	
		final CClub_GetRecordByPlayer req = new Gson().fromJson(message, CClub_GetRecordByPlayer.class);
		
		ClubMember myClubMember = ClubMgr.getInstance().getClubMemberMgr().find(player.getPid(), req.clubId, Club_Player_Status.PLAYER_JIARU);
    	if (null == myClubMember) {
    		request.error(ErrorCode.NotAllow, "CClubGetPlayerRecord not find myClubMember");
			return;
		}
    	
    	if (!myClubMember.isMinister()) {
			request.error(ErrorCode.CLUB_NOTMINISTER, "CClubGetPlayerRecord you have not minister");
			return;
		}
    	
    	ClubMgr.getInstance().getClubRankMgr().getRecordByPlayer(request, req);
	}

}
