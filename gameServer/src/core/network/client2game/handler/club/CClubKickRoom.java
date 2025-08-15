package core.network.client2game.handler.club;

import java.io.IOException;

import business.global.shareroom.ShareRoom;
import business.global.shareroom.ShareRoomMgr;
import business.rocketmq.bo.MqClubKickNotifyBo;
import business.rocketmq.bo.MqUnionKickNotifyBo;
import business.rocketmq.constant.MqTopic;
import cenum.RoomTypeEnum;
import com.ddm.server.common.Config;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.global.room.NormalRoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.RoomImpl;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.club.Club_define.Club_Player_Status;
import jsproto.c2s.iclass.club.CClub_KickRoom;

/**
 * 踢出房间
 * 
 * @author Huaxing
 *
 */
public class CClubKickRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {
		final CClub_KickRoom req = new Gson().fromJson(message, CClub_KickRoom.class);
		if(Config.isShare()){
			int opMinister = ClubMgr.getInstance().getClubMemberMgr().getMinister(req.clubId, player.getPid());
			if (opMinister <= 0) {
				request.error(ErrorCode.CLUB_NOTMINISTER, "you not minister");
				return;
			}

			int posIndex = req.posIndex;

			if (posIndex < 0) {
				request.error(ErrorCode.NotAllow, "posIndex:" + posIndex);
				return;
			}

			ShareRoom room = ShareRoomMgr.getInstance().getShareRoomByKey(req.roomKey);
			if (null == room) {
				request.error(ErrorCode.NotAllow, "CClubKickRoom not find room:" + req.roomKey);
				return;
			}
			if (room.isNoneRoom() || !RoomTypeEnum.CLUB.equals(room.getRoomTypeEnum()) || room.getSpecialRoomId() != req.clubId) {
				request.error(ErrorCode.NotAllow, "CClubKickRoom not find room:" + req.roomKey);
				return;
			}
			//通知mq
			MqProducerMgr.get().send(MqTopic.CLUB_KICK_NOTIFY, new MqClubKickNotifyBo(req.clubId, req.roomKey, req.posIndex, player.getPid(), player.getName()));
			request.response();
		} else {
			int opMinister = ClubMgr.getInstance().getClubMemberMgr().getMinister(req.clubId, player.getPid());
			if (opMinister <= 0) {
				request.error(ErrorCode.CLUB_NOTMINISTER, "you not minister");
				return;
			}

			int posIndex = req.posIndex;

			if (posIndex < 0) {
				request.error(ErrorCode.NotAllow, "posIndex:" + posIndex);
				return;
			}

			RoomImpl roomImpl = NormalRoomMgr.getInstance().getNoneRoomByKey(req.roomKey);
			if (null == roomImpl) {
				request.error(ErrorCode.NotAllow, "CClubKickRoom not find room:" + req.roomKey);
				return;
			}
			if (roomImpl.isNoneRoom() || !RoomTypeEnum.CLUB.equals(roomImpl.getRoomTypeEnum()) || roomImpl.getSpecialRoomId() != req.clubId) {
				request.error(ErrorCode.NotAllow, "CClubKickRoom not find room:" + req.roomKey);
				return;
			}


			AbsBaseRoom room = ((AbsBaseRoom) roomImpl);

			AbsRoomPos roomPos = room.getRoomPosMgr().getPosByPosID(posIndex);
			if (roomPos == null) {
				request.error(ErrorCode.NotAllow, "pos == null ");
				return;
			}

			int kickMinister = ClubMgr.getInstance().getClubMemberMgr().getMinister(req.clubId, roomPos.getPid());
			if (kickMinister <= 0 || kickMinister > opMinister) {
				String msg = String.format("您被亲友圈管理@%s，从房间@%s内踢出", player.getName(), req.roomKey);
				SData_Result result = room.specialKickOut(player.getPid(), posIndex, msg);
				if (ErrorCode.Success.equals(result.getCode())) {
					request.response();
				} else {
					request.error(result.getCode(), result.getMsg());
				}
			} else {
				request.error(ErrorCode.CLUB_MEMBER_SAME_POST_TYPE, "CLUB_MEMBER_SAME_POST_TYPE");
				return;
			}
		}
	}
}
