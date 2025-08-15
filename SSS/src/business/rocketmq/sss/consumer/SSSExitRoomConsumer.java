package business.rocketmq.sss.consumer;

import business.rocketmq.constant.MqTopic;
import business.rocketmq.consumer.BaseExitRoomConsumer;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import core.server.sss.SSSAPP;

/**
 * @author : xushaojun
 * create at:  2020-09-08  10:00
 * @description: 退出房间
 */
@Consumer(topic = MqTopic.BASE_EXIT_ROOM, id = SSSAPP.gameTypeId)
public class SSSExitRoomConsumer extends BaseExitRoomConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        super.action(body, SSSAPP.GameType().getId());
    }
}
