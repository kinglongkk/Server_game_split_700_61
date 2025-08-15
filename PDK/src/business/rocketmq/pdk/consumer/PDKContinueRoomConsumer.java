package business.rocketmq.pdk.consumer;

import business.rocketmq.constant.MqTopic;
import business.rocketmq.consumer.BaseContinueRoomConsumer;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import core.server.pdk.PDKAPP;

/**
 * @author : xushaojun
 * create at:  2020-11-4  11:17
 * @description: 继续房间
 */
@Consumer(topic = MqTopic.BASE_CONTINUE_ROOM, id = PDKAPP.gameTypeId)
public class PDKContinueRoomConsumer extends BaseContinueRoomConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) throws ClassNotFoundException {
        super.action(body, PDKAPP.GameType());

    }
}
