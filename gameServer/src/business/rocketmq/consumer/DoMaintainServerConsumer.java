package business.rocketmq.consumer;

import BaseCommon.CommLog;
import business.global.GM.MaintainServerMgr;
import business.global.sharegm.ShareNodeServerMgr;
import business.rocketmq.bo.MqDoMaintainServerBo;
import business.rocketmq.bo.MqUrgentMaintainServerBo;
import business.rocketmq.constant.MqTopic;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import com.ddm.server.mq.factory.MqConsumerTopicFactory;
import core.dispatcher.RegMqHandler;
import core.dispatcher.RegNetPack;
import jsproto.c2s.iclass.registry.CRegistry_ServerOffline;

/**
 * @author : xushaojun
 * create at:  2020-10-30  16:30
 * @description: 游戏维护
 */
@Consumer(topic = MqTopic.DO_MAINTAIN_SERVER)
public class DoMaintainServerConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        MqDoMaintainServerBo bo =(MqDoMaintainServerBo) body;
        if (bo.getMaintainServerInt() == 1) {
            CommLog.info("开始维护");
            boolean maintain = MaintainServerMgr.getInstance().checkRestartServerTime();
            if(maintain) {
                MqConsumerTopicFactory.getInstance().stopCurConnect();
            }
        } else if (bo.getMaintainServerInt() == 2) {
            CommLog.info("结束维护");
            MaintainServerMgr.getInstance().setMaintainServer(false);
        }
    }
}
