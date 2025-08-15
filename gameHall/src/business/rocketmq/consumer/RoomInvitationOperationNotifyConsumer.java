package business.rocketmq.consumer;

import BaseCommon.CommLog;
import business.global.shareroom.ShareRoom;
import business.global.shareroom.ShareRoomMgr;
import business.global.union.UnionMgr;
import business.player.Player;
import business.player.PlayerMgr;
import business.rocketmq.bo.MqRoomInvitationOperationNotifyBo;
import business.rocketmq.bo.MqUnionMemberNotifyBo;
import business.rocketmq.constant.MqTopic;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import com.ddm.server.common.utils.JsonUtil;
import core.dispatch.DispatcherComponent;
import core.dispatch.event.room.RoomInvitationOperationEvent;
import core.dispatch.event.union.UnionNotify2AllByClubEvent;

/**
 * @author : xushaojun
 * create at:  2020-09-04  10:00
 * @description: 邀请在线玩家通知
 */
@Deprecated
//@Consumer(topic = MqTopic.ROOM_INVITATION_OPERATION_NOTIFY)
public class RoomInvitationOperationNotifyConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        MqRoomInvitationOperationNotifyBo bo = (MqRoomInvitationOperationNotifyBo) body;
        CommLogD.info("邀请在线玩家通知[{}]", JsonUtil.toJson(bo));
        Player player = PlayerMgr.getInstance().getOnlinePlayerByPid(bo.getPid());
        ShareRoom shareRoom = ShareRoomMgr.getInstance().getShareRoomByKey(bo.getRoomKey());
        // 通知玩家邀请
        DispatcherComponent.getInstance().publish(new RoomInvitationOperationEvent(bo.getPid(), player.getPid(), bo.getClubId(), bo.getUnionId(), shareRoom.getRoomKey(), shareRoom.getBaseRoomConfigure().getGameType().getId(), player.getName(), shareRoom.getBaseRoomConfigure().getBaseCreateRoom(), shareRoom.getRoomPidAll()));
    }
}
