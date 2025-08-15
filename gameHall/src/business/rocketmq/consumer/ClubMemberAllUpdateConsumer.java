package business.rocketmq.consumer;

import business.global.club.ClubMemberMgr;
import business.global.club.ClubMgr;
import business.global.shareclub.ShareClubMemberMgr;
import business.player.Player;
import business.player.PlayerMgr;
import business.rocketmq.bo.MqClubMemberAllUpdateNotifyBo;
import business.rocketmq.bo.MqClubMemberNotifyBo;
import business.rocketmq.constant.MqTopic;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import com.ddm.server.common.utils.JsonUtil;
import jsproto.c2s.cclass.club.Club_define;

/**
 * @author : xushaojun
 * create at:  2020-08-20  14:00
 * @description: 亲友圈房间成员消息通知
 */
@Consumer(topic = MqTopic.CLUB_MEMBER_ALL_UPDATE)
public class ClubMemberAllUpdateConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        MqClubMemberAllUpdateNotifyBo mqClubRoomResultBo = (MqClubMemberAllUpdateNotifyBo) body;
        ClubMgr.getInstance().getClubMemberMgr().onUpdateAllMemberShare();
    }
}
