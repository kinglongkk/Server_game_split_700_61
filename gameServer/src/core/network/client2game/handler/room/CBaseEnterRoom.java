package core.network.client2game.handler.room;

import business.global.GM.MaintainGameMgr;
import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.global.config.GameListConfigMgr;
import business.global.room.NormalRoomMgr;
import business.global.room.base.RoomImpl;
import business.global.shareclub.ShareClubMemberMgr;
import business.global.sharegm.ShareNodeServerMgr;
import business.global.shareroom.ShareRoom;
import business.global.shareroom.ShareRoomMgr;
import business.player.Player;
import business.rocketmq.bo.MqAbsRequestBo;
import business.rocketmq.constant.MqTopic;
import business.shareplayer.ShareNode;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.config.server.GameTypeMgr;
import core.network.client2game.ClientAcceptor;
import core.network.client2game.handler.BaseHandler;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.cclass.GameTypeUrl;
import jsproto.c2s.cclass.club.Club_define;
import jsproto.c2s.iclass.room.CBase_EnterRoom;

import java.io.IOException;
import java.util.Objects;

/**
 * 进入房间
 * @author Administrator
 *
 */
public class CBaseEnterRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {
		final CBase_EnterRoom req = new Gson().fromJson(message, CBase_EnterRoom.class);
		CommLogD.info("进入房间开始");
		if(Config.isShare()){
			ShareRoom shareRoom = ShareRoomMgr.getInstance().getShareRoomByKey(req.getRoomKey());
			if (shareRoom == null) {
				request.error(ErrorCode.Room_NOT_Find, "Room_NOT_Find roomKey:{%s}", req.getRoomKey());
				return;
			}
			GameType gameType = GameTypeMgr.getInstance().gameType(shareRoom.getBaseRoomConfigure().getGameType().getId());
			long requestId = System.nanoTime();
			CommLogD.info("进入房间开始[{}],请求标识[{}]", gameType.getName(), requestId);
			if(GameListConfigMgr.getInstance().checkIsLiveByRoom(shareRoom)) {
				//检查游戏是否在维护中
				SData_Result result = MaintainGameMgr.getInstance().checkMaintainGame(gameType.getId(), player);
				if (!ErrorCode.Success.equals(result.getCode())) {
					request.error(ErrorCode.Game_Maintain, MaintainGameMgr.getInstance().getMaintainGameContent(gameType.getId()));
					return;
				}
				GameTypeUrl gameTypeUrl = GameListConfigMgr.getInstance().getByRoom(shareRoom);
				ClubMember clubMember = ShareClubMemberMgr.getInstance().getClubMember(req.getClubId(), player.getPid());
				if (Objects.nonNull(clubMember)) {
					if (clubMember.getClubMemberBO().getStatus() == Club_define.Club_Player_Status.PLAYER_TUICHU_WEIPIZHUN.value()) {
						request.error(ErrorCode.NoPower_RoomJoinner, "PLAYER_TUICHU_WEIPIZHUN");
						return;
					}
					SData_Result dataResult = ClubMgr.getInstance().getClubMemberMgr().checkSportsPointWarning(clubMember);
					if(!ErrorCode.Success.equals(dataResult.getCode())){
						request.error(ErrorCode.WarningSport_RoomJoinner, dataResult.getMsg());
						return;
					}
					SData_Result dataResultPersonal=ClubMgr.getInstance().getClubMemberMgr().checkPersonalSportsPointWarning(clubMember);
					if(!ErrorCode.Success.equals(dataResultPersonal.getCode())){
						request.error(ErrorCode.PersonalWarningSport_RoomJoinner, dataResultPersonal.getMsg());
						return;
					}
				}
				ShareNode shareNode = new ShareNode("", gameTypeUrl.getWebSocketUrl(), gameTypeUrl.getGameServerIP(), gameTypeUrl.getGameServerPort());
				MqAbsRequestBo mqAbsRequestBo = new MqAbsRequestBo(player.getPid(), gameType.getName(), gameType.getId(), message, request.getHeader().event, shareNode);
				mqAbsRequestBo.setRequestId(requestId);
				mqAbsRequestBo.setShareNodeFrom(ShareNodeServerMgr.getInstance().getThisNode());
				//推送到MQ
				MqProducerMgr.get().send(MqTopic.BASE_ENTER_ROOM + gameType.getId(), mqAbsRequestBo);
				request.response();
			} else {
				request.error(ErrorCode.Server_Maintain, String.valueOf(System.currentTimeMillis()/1000 + 300));
			}
		} else {
				RoomImpl roomImpl = NormalRoomMgr.getInstance().getNoneRoomByKey(req.getRoomKey());
				if (roomImpl != null && null != roomImpl.getBaseRoomConfigure() && null != roomImpl.getBaseRoomConfigure().getGameType()) {
					GameType gameType = GameTypeMgr.getInstance().gameType(roomImpl.getBaseRoomConfigure().getGameType().getId());
					ClubMember clubMember = ClubMgr.getInstance().getClubMemberMgr().getClubMember(req.getClubId(), player.getPid());
					if (Objects.nonNull(clubMember)) {
						if (clubMember.getClubMemberBO().getStatus() == Club_define.Club_Player_Status.PLAYER_TUICHU_WEIPIZHUN.value()) {
							request.error(ErrorCode.NoPower_RoomJoinner, "PLAYER_TUICHU_WEIPIZHUN");
							return;
						}
					}
					SData_Result dataResult=ClubMgr.getInstance().getClubMemberMgr().checkSportsPointWarning(clubMember);
					if(!ErrorCode.Success.equals(dataResult.getCode())){
						request.error(ErrorCode.WarningSport_RoomJoinner, dataResult.getMsg());
						return;
					}

					SData_Result dataResultPersonal=ClubMgr.getInstance().getClubMemberMgr().checkPersonalSportsPointWarning(clubMember);
					if(!ErrorCode.Success.equals(dataResultPersonal.getCode())){
						request.error(ErrorCode.PersonalWarningSport_RoomJoinner, dataResultPersonal.getMsg());
						return;
					}
					if (Objects.nonNull(gameType)) {
						String gameTypeName = gameType.getName().toLowerCase();
						BaseHandler handler = (BaseHandler) ClientAcceptor.getInstance().getHandle(gameTypeName + ".c" + gameTypeName + "enterroom");
						handler.handle(request, message);
						return;
					}
					request.response();
				} else {
					request.error(ErrorCode.NotFind_Room, "NotFind_Room");
				}
			}

	}

}
