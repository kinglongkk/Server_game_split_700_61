package core.network.client2game.handler.qzmj;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.feature.PlayerGoldRoom;
import business.qzmj.c2s.iclass.CQZMJ_CreateRoom;
import cenum.PrizeType;
import core.config.refdata.RefDataMgr;
import core.config.refdata.ref.RefPractice;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.qzmj.QZMJAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.cclass.room.RobotRoomConfig;
import jsproto.c2s.iclass.room.CBase_GoldRoom;

/**
 * 创建房间
 * 
 * @author Administrator
 *
 */
public class CQZMJGoldRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {

		final CBase_GoldRoom clientPack = new Gson().fromJson(message, CBase_GoldRoom.class);
		RefPractice data = RefDataMgr.get(RefPractice.class, clientPack.getPracticeId());
		if (data == null) {
			request.error(ErrorCode.NotAllow, "CQZMJGoldRoom do not find practiceId");
			return;
		}
		// 游戏配置
		CQZMJ_CreateRoom createClientPack = new CQZMJ_CreateRoom();
		// 公共房间配置
		BaseRoomConfigure<CQZMJ_CreateRoom> configure = new BaseRoomConfigure<CQZMJ_CreateRoom>(PrizeType.Gold,
				QZMJAPP.GameType(), createClientPack.clone(), new RobotRoomConfig(data.getBaseMark(),data.getMin(),data.getMax(),clientPack.getPracticeId()));
		SData_Result resule = player.getFeature(PlayerGoldRoom.class).createAndQuery(configure);
		if (ErrorCode.Success.equals(resule.getCode())) {
			request.response(resule.getData());
		} else {
			request.error(resule.getCode(), resule.getMsg());
		}
	}
}
