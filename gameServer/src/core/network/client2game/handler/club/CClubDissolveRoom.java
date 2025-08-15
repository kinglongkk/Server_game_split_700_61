package core.network.client2game.handler.club;

import java.io.IOException;

import BaseCommon.CommLog;
import business.global.shareroom.ShareRoom;
import business.global.shareroom.ShareRoomMgr;
import business.rocketmq.bo.MqClubDissolveRoomNotifyBo;
import business.rocketmq.constant.MqTopic;
import cenum.RoomTypeEnum;
import com.ddm.server.common.Config;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.club.ClubMgr;
import business.global.room.NormalRoomMgr;
import business.global.room.base.RoomImpl;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.club.Club_define;
import jsproto.c2s.iclass.club.CClub_DissolveRoom;

/**
 * 亲友圈解散房间
 * @author Huaxing
 *
 */
public class CClubDissolveRoom extends PlayerHandler {


    @SuppressWarnings("rawtypes")
	@Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	
    	final CClub_DissolveRoom req = new Gson().fromJson(message, CClub_DissolveRoom.class);
    	if(Config.isShare()){
			String roomkey = req.roomKey;
			int minister = ClubMgr.getInstance().getClubMemberMgr().getMinister(req.clubId, player.getPid());
			if (minister <= 0) {
				request.error(ErrorCode.CLUB_NOTMINISTER, "you not minister");
				return;
			}
			ShareRoom room = ShareRoomMgr.getInstance().getShareRoomByKey(roomkey);
			if (null == room) {
				request.error(ErrorCode.ExitROOM_ERROR_NOTFINDROOM, "CBaseDissolveRoom not find roomKey :" + roomkey);
				return;
			}
			if (room.getSpecialRoomId() != req.clubId || !RoomTypeEnum.CLUB.equals(room.getRoomTypeEnum())) {
				request.error(ErrorCode.ExitROOM_ERROR_NOTFINDROOM, "CBaseDissolveRoom not find roomKey :" + roomkey);
				return;
			}
			//通知mq
			MqProducerMgr.get().send(MqTopic.CLUB_DISSOLVE_ROOM_NOTIFY, new MqClubDissolveRoomNotifyBo(req.clubId, req.roomKey, player.getPid(), player.getName()));
			request.response();
		} else {
			String roomkey = req.roomKey;
			int minister = ClubMgr.getInstance().getClubMemberMgr().getMinister(req.clubId, player.getPid());
			if (minister <= 0) {
				request.error(ErrorCode.CLUB_NOTMINISTER, "you not minister");
				return;
			}
			RoomImpl room = NormalRoomMgr.getInstance().getNoneRoomByKey(roomkey);
			if (null == room) {
				request.error(ErrorCode.ExitROOM_ERROR_NOTFINDROOM, "CBaseDissolveRoom not find roomKey :" + roomkey);
				return;
			}
			if (room.getSpecialRoomId() != req.clubId || !RoomTypeEnum.CLUB.equals(room.getRoomTypeEnum())) {
				request.error(ErrorCode.ExitROOM_ERROR_NOTFINDROOM, "CBaseDissolveRoom not find roomKey :" + roomkey);
				return;
			}
//		房间@房间ID已被亲友圈管理@管理名称 解散
//		房间@房间ID已被群主@群主名称 解散
			String ministerName = minister == Club_define.Club_MINISTER.Club_MINISTER_CREATER.value() ? "圈主" : "管理员";
			String msg = String.format("房间@%s已被亲友圈%s@%s 解散", roomkey, ministerName, player.getName());
			SData_Result result = room.specialDissolveRoom(req.clubId, RoomTypeEnum.CLUB, minister, msg);
			if (ErrorCode.Success.equals(result.getCode())) {
				CommLog.info("CClubDissolveRoom Success Pid:{},ClubId:{},RoomKey:{},minister:{},msg:{}", player.getPid(), req.clubId, req.roomKey, minister, msg);
				request.response();
			} else {
				request.error(result.getCode(), result.getMsg());
			}
		}
    }
}
