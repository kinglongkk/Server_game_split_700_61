package core.network.client2game.handler.union;

import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.global.room.NormalRoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.RoomImpl;
import business.global.shareroom.ShareRoom;
import business.global.shareroom.ShareRoomMgr;
import business.global.union.UnionMember;
import business.global.union.UnionMgr;
import business.player.Player;
import business.rocketmq.bo.MqUnionKickNotifyBo;
import business.rocketmq.constant.MqTopic;
import cenum.RoomTypeEnum;
import com.ddm.server.common.Config;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.club.Club_define.Club_Player_Status;
import jsproto.c2s.cclass.union.UnionDefine;
import jsproto.c2s.iclass.club.CClub_KickRoom;
import jsproto.c2s.iclass.union.CUnion_KickRoom;

import java.io.IOException;

/**
 * 踢出房间
 *
 * @author Huaxing
 */
public class CUnionKickRoom extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CUnion_KickRoom req = new Gson().fromJson(message, CUnion_KickRoom.class);
        if(Config.isShare()){
            int opMinister = UnionMgr.getInstance().getUnionMemberMgr().getMinister(player.getPid(), req.getClubId(), req.getUnionId());
            if (opMinister <= 0) {
                request.error(ErrorCode.UNION_NOT_EXIST_MEMBER, "UNION_NOT_EXIST_MEMBER");
                return;
            }
            int posIndex = req.getPosIndex();
            if (posIndex < 0) {
                request.error(ErrorCode.NotAllow, "posIndex:" + posIndex);
                return;
            }
            ShareRoom room = ShareRoomMgr.getInstance().getShareRoomByKey(req.getRoomKey());
            if (null == room) {
                request.error(ErrorCode.NotAllow, "CUnionKickRoom not find room:" + req.getRoomKey());
                return;
            }
            if (room.isNoneRoom() || !RoomTypeEnum.UNION.equals(room.getRoomTypeEnum())) {
                request.error(ErrorCode.NotAllow, "CUnionKickRoom not find room:" + req.getRoomKey());
                return;
            }
            //通知mq
            MqProducerMgr.get().send(MqTopic.UNION_KICK_NOTIFY, new MqUnionKickNotifyBo(req.getRoomKey(), req.getPosIndex(), req.getUnionId(), req.getClubId(), player.getPid(), player.getName()));
            request.response();
        } else {
            int opMinister = UnionMgr.getInstance().getUnionMemberMgr().getMinister(player.getPid(), req.getClubId(), req.getUnionId());
            if (opMinister <= 0) {
                request.error(ErrorCode.UNION_NOT_EXIST_MEMBER, "UNION_NOT_EXIST_MEMBER");
                return;
            }
            int posIndex = req.getPosIndex();
            if (posIndex < 0) {
                request.error(ErrorCode.NotAllow, "posIndex:" + posIndex);
                return;
            }

            RoomImpl roomImpl = NormalRoomMgr.getInstance().getNoneRoomByKey(req.getRoomKey());
            if (null == roomImpl) {
                request.error(ErrorCode.NotAllow, "CUnionKickRoom not find room:" + req.getRoomKey());
                return;
            }
            if (roomImpl.isNoneRoom() || !RoomTypeEnum.UNION.equals(roomImpl.getRoomTypeEnum())) {
                request.error(ErrorCode.NotAllow, "CUnionKickRoom not find room:" + req.getRoomKey());
                return;
            }

            AbsBaseRoom room = ((AbsBaseRoom) roomImpl);

            AbsRoomPos roomPos = room.getRoomPosMgr().getPosByPosID(posIndex);
            if (roomPos == null || roomPos.getClubMemberBO() == null) {
                request.error(ErrorCode.NotAllow, "pos == null ");
                return;
            }
            long clubId = roomPos.getClubMemberBO().getClubID();

            // 被踢的人
            int kickMinister = UnionMgr.getInstance().getUnionMemberMgr().getMinister(roomPos.getPid(), clubId, req.getUnionId());


            if (kickMinister <= 0 || opMinister > kickMinister) {
                String msg = String.format("您被赛事管理@%s，从房间@%s内踢出", player.getName(), req.getRoomKey());
                SData_Result result = room.specialKickOut(player.getPid(), posIndex, msg);
                if (ErrorCode.Success.equals(result.getCode())) {
                    request.response();
                } else {
                    request.error(result.getCode(), result.getMsg());
                }
            } else {
                request.error(ErrorCode.UNION_MEMBER_SAME_POST_TYPE, "pos == null ");
                return;
            }
        }

    }
}
