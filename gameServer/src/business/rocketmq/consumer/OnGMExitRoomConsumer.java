package business.rocketmq.consumer;

import BaseThread.ThreadManager;
import business.global.club.ClubMgr;
import business.global.room.NormalRoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.RoomImpl;
import business.global.sharegm.ShareNodeServerMgr;
import business.player.Player;
import business.player.PlayerMgr;
import business.rocketmq.bo.MqAbsRequestBo;
import business.rocketmq.bo.MqClubKickNotifyBo;
import business.rocketmq.bo.MqOnGMExitRoomMsg;
import business.rocketmq.bo.MqRoomResultBo;
import business.rocketmq.constant.MqTopic;
import cenum.RoomTypeEnum;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import core.network.http.proto.SData_Result;
import core.network.http.proto.ZleData_Result;

/**
 * @author : xushaojun
 * create at:  2020-11-27  10:00
 * @description: 后台踢出玩家
 */
@Consumer(topic = MqTopic.ON_GM_EXIT_ROOM)
public class OnGMExitRoomConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        MqOnGMExitRoomMsg bo = (MqOnGMExitRoomMsg) body;
        //检测当前节点
        if(ShareNodeServerMgr.getInstance().checkCurrentNode(bo.getShareNode().getIp(), bo.getShareNode().getPort())){
            // 用户信息
            Player player = PlayerMgr.getInstance().getPlayer(bo.getPid());
            if (player == null) {
                return;
            }
            player.onGMExitRoom();
        }
    }

}
