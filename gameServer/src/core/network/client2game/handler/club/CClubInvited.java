package core.network.client2game.handler.club;

import java.io.IOException;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.global.club.ClubMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.club.SClub_InvitedList;

/**
 * 请求是否有邀请加入俱乐部
 * @author zaf
 *
 */
public class CClubInvited extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		request.response();
    	player.pushProto(SClub_InvitedList.make(ClubMgr.getInstance().onClubInvited(player.getPid())));
	}

}
