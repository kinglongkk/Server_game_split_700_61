package business.rocketmq.pdk.consumer;

import business.rocketmq.constant.MqTopic;
import business.rocketmq.consumer.BaseExitRoomConsumer;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import core.server.pdk.PDKAPP;

/**
 * @author : xushaojun
 * create at:  2020-09-08  10:00
 * @description: 退出房间
 */
@Consumer(topic = MqTopic.BASE_EXIT_ROOM, id = PDKAPP.gameTypeId)
public class PDKExitRoomConsumer extends BaseExitRoomConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        super.action(body, PDKAPP.GameType().getId());
    }
}
