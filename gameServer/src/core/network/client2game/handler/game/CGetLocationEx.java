package core.network.client2game.handler.game;

import java.io.IOException;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.CGet_LocationEx;
import org.apache.commons.lang3.StringUtils;

/*
 * 请求玩家获取定位
 * */

public class CGetLocationEx extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		// TODO 自动生成的方法存根		
		final CGet_LocationEx req = new Gson().fromJson(message, CGet_LocationEx.class);
		double latitude = req.Latitude;
		double longitude = req.Longitude;
    	String address = req.Address;
    	boolean isGetError = req.isGetError;
    	if (StringUtils.isEmpty(address) || "null".equals(address)) {
    		isGetError = true;
		}
    	player.setLocationInfo(address, latitude, longitude, isGetError);
    	request.response();
    	
	}

}
