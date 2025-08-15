package business.rocketmq.consumer;

import BaseCommon.CommLog;
import business.player.Player;
import business.player.PlayerMgr;
import business.rocketmq.bo.MqPlayerChangeNotifyBo;
import business.rocketmq.constant.MqTopic;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import com.ddm.server.common.utils.JsonUtil;

import java.util.List;

/**
 * @author : xushaojun
 * create at:  2020-09-24  16:51
 * @description: 玩家信息变化通知
 */
@Consumer(topic = MqTopic.PLAYER_CHANGE_NOTIFY)
public class PlayerChangeConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        MqPlayerChangeNotifyBo bo = (MqPlayerChangeNotifyBo) body;
        Player player = PlayerMgr.getInstance().getPlayer(bo.getPid());
        if (player != null) {
            CommLogD.info("玩家信息变化通知[{}][{}]", JsonUtil.toJson(bo));
            player.pushPropertiesMq((List<jsproto.c2s.cclass.Player.Property>) bo.getBaseSendMsg());
        }
    }
}
