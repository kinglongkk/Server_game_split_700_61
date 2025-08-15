package core.network.client2game.handler.game;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.C1008_LoginXL;

/**
 * 设置闲聊ID
 * @author Huaxing
 *
 */
public class CPlayerXLUnionid extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws WSException, IOException {
		final C1008_LoginXL req = new Gson().fromJson(message, C1008_LoginXL.class);
		if (StringUtils.isEmpty(req.xlUnionid)) {
			request.error(ErrorCode.Error_XL,"isEmpty");
			return;
		}
		if (StringUtils.isNotEmpty(player.getPlayerBO().getXl_unionid())) {
			request.error(ErrorCode.Exist_XL, "isNotEmpty");
			return;
		}
    	player.getPlayerBO().saveXl_unionid(req.xlUnionid);
		request.response();
	}

}
