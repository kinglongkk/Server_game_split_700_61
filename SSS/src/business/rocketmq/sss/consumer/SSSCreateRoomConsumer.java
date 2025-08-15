package business.rocketmq.sss.consumer;

import BaseCommon.CommLog;
import business.global.sharegm.ShareNodeServerMgr;
import business.sss.c2s.iclass.CSSS_CreateRoom;
import business.rocketmq.bo.MqAbsRequestBo;
import business.rocketmq.constant.MqTopic;
import business.rocketmq.consumer.BaseCreateRoomConsumer;
import cenum.PrizeType;
import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.rocketmq.MqConsumerHandler;
import com.google.gson.Gson;
import core.server.sss.SSSAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

/**
 * @author : xushaojun
 * create at:  2020-08-19  11:17
 * @description: 红中麻将创建房间
 */
@Consumer(topic = MqTopic.BASE_CREATE_ROOM, id = SSSAPP.gameTypeId)
public class SSSCreateRoomConsumer extends BaseCreateRoomConsumer implements MqConsumerHandler {


    @Override
    public void action(Object body) throws ClassNotFoundException {
        MqAbsRequestBo mqAbsRequestBo = (MqAbsRequestBo) body;
        //判断游戏和请求创建节点一致
        if (mqAbsRequestBo.getGameTypeId() == SSSAPP.GameType().getId() && ShareNodeServerMgr.getInstance().checkCurrentNode(mqAbsRequestBo.getShareNode().getIp(), mqAbsRequestBo.getShareNode().getPort())) {
//            CommLog.info("创建房间[{}]", mqAbsRequestBo.getGameTypeName());
            final CSSS_CreateRoom clientPack = new Gson().fromJson(mqAbsRequestBo.getBody(),
                    CSSS_CreateRoom.class);
            // 公共房间配置
            BaseRoomConfigure<CSSS_CreateRoom> configure = new BaseRoomConfigure<>(
                    PrizeType.RoomCard,
                    SSSAPP.GameType(),
                    clientPack.clone());
            super.action(body, SSSAPP.GameType().getId(), configure);
        }
    }
}
