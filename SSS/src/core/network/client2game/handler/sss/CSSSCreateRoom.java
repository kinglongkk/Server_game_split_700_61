package core.network.client2game.handler.sss;

import business.player.Player;
import business.player.feature.PlayerRoom;
import business.sss.c2s.iclass.CSSS_CreateRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.sss.SSSAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;


import java.io.IOException;

/**
 * 创建房间
 * @author Huaxing
 *
 */
public class CSSSCreateRoom extends PlayerHandler {
	

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	
    	final CSSS_CreateRoom clientPack = new Gson().fromJson(message, CSSS_CreateRoom.class);

		// 公共房间配置
		BaseRoomConfigure<CSSS_CreateRoom> configure = new BaseRoomConfigure<CSSS_CreateRoom>(
				PrizeType.RoomCard,
				SSSAPP.GameType(),
				clientPack.clone());

		SData_Result resule = player.getFeature(PlayerRoom.class).createRoomAndConsumeCard(configure);
		if (ErrorCode.Success.equals(resule.getCode())) {
			request.response(resule.getData());
		} else {
			request.error(resule.getCode(),resule.getMsg());
		}
    	
    }
}
