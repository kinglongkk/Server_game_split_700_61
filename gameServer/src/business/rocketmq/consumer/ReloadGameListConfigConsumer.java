package business.rocketmq.consumer;

import BaseCommon.CommLog;
import BaseThread.ThreadManager;
import business.global.config.GameListConfigMgr;
import business.global.room.NormalRoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.RoomImpl;
import business.global.union.UnionMgr;
import business.rocketmq.bo.MqAbsRequestBo;
import business.rocketmq.bo.MqRoomResultBo;
import business.rocketmq.bo.MqUnionKickNotifyBo;
import business.rocketmq.constant.MqTopic;
import cenum.RoomTypeEnum;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import core.network.http.proto.SData_Result;

/**
 * @author : xushaojun
 * create at:  2020-10-12  10:00
 * @description: 重新加载游戏配置
 */
@Consumer(topic = MqTopic.HTTP_RELOAD_GAME_LIST_CONFIG)
public class ReloadGameListConfigConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) {
        CommLogD.info("游戏配置更新");
        GameListConfigMgr.getInstance().init();
    }

}
