package core.network.client2game.handler.base;

import java.io.IOException;
import java.util.UUID;

import business.global.GM.MaintainServerMgr;
import org.apache.commons.lang3.StringUtils;

import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.PlayerMgr;
import cenum.DefaultEnum;
import core.network.client2game.ClientSession;
import core.network.client2game.handler.BaseHandler;
import core.server.OpenSeverTime;
import jsproto.c2s.iclass.C1008_LoginXL;
import jsproto.c2s.iclass.S1004_Login;

/**
 * 闲聊登录
 * 
 * @author Administrator
 *
 */
public class C1008LoginXL extends BaseHandler {

	@Override
	public void handle(WebSocketRequest request, String data) throws IOException {
		final C1008_LoginXL req = new Gson().fromJson(data, C1008_LoginXL.class);
		// 闲聊Unionid错误
		if (StringUtils.isEmpty(req.xlUnionid)) {
			request.error(ErrorCode.Error_XL, "Error_XL");
			return;
		}
		// 人数限制
		int onlineCount = PlayerMgr.getInstance().getOnlinePlayers().size();
		if (onlineCount >= 20000) {
			request.error(ErrorCode.ErrorSysMsg, "World_MaxOnLine");
			return;
		}
		
		Player player = PlayerMgr.getInstance().getPlayerXL(req.xlUnionid);
		if (null == player) {
			request.error(ErrorCode.Not_Exist_XL, "Not_Exist_XL");
			return;
		}
		// 检查是否处于维护中
		if (!MaintainServerMgr.getInstance().checkUnderMaintenance(request,player)) {
			// 维护中
			return;
		}
		ClientSession session = (ClientSession) request.getSession();
		session.setValid(true);
		session.setAccountID(player.getAccountID());
		session.setPlayerSid(player.getPlayerBO().getSid());
		session.setWxUnionid(player.getPlayerBO().getWx_unionid());
		PlayerMgr playerMgr = PlayerMgr.getInstance();
		if (player.isBannedLogin(request)) {
			return;
		}
		playerMgr.connectPlayer(session, player);
		// 设置UUID
		player.setUUID(UUID.randomUUID().toString().replaceAll("-", ""));
		S1004_Login resultInfo = new S1004_Login();
		resultInfo.time = CommTime.nowMS();
		resultInfo.timeZone = CommTime.timezone().getRawOffset();
		resultInfo.startServerTime = OpenSeverTime.getInstance().getStartServerTime();
		resultInfo.defaultFamilyID = DefaultEnum.FAMILY_ID.value();
		resultInfo.isNeedCreateRole = 0;
		resultInfo.accountID = player.getAccountID();
		// 发送前台
		request.response(resultInfo);
	}
}
