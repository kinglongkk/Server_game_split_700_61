package com.ddm.server.common.rocketmq;

import BaseCommon.CommLog;
import com.google.gson.Gson;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author xsj
 * @date 2020/8/12 15:32
 * @description mq生产者管理类
 */
public class MqProducerMgr {
    private String namesrvAddr;
    private String groupName;
    private Integer retryTimesWhenSendAsyncFailed;
    private MQProducer mqProducer;

    private MqProducerMgr() {

    }

    public static MqProducerMgr get() {
        return SingleCase.INSTANCE;
    }

    public void loadConfig(String path) throws Exception {
        loadConfig(new FileInputStream(path));
        mqProducer = mqProducer();
    }

    private void loadConfig(InputStream in) throws Exception {
        Properties pro = new Properties();
        pro.load(in);
        this.retryTimesWhenSendAsyncFailed = Integer.parseInt(pro.getProperty("mq.retry.times"));
        this.namesrvAddr = pro.getProperty("mq.namesrv.addr");
        this.groupName = pro.getProperty("mq.group.name");
    }

    private DefaultMQProducer mqProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        // 设置NameServer的地址
        producer.setNamesrvAddr(namesrvAddr);
        // 启动Producer实例
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            CommLog.error(e.getMessage(), e);
        }
        producer.setRetryTimesWhenSendAsyncFailed(retryTimesWhenSendAsyncFailed);
        return producer;
    }

    /**
     * 发送到数据到MQ
     *
     * @param topic 主题名
     * @param body  消息内容
     */
    public void send(String topic, Object body) {
        try {
            byte[] bytes;
            if (body != null) {
                MqAbsBo mqAbsBo = (MqAbsBo) body;
                mqAbsBo.setClazzName(body.getClass().getName());
                bytes = new Gson().toJson(body).getBytes("UTF-8");
            } else {
                bytes = null;
            }
            Message msg = new Message(topic, "tagTest", "keyTest", bytes);
            mqProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                }

                @Override
                public void onException(Throwable e) {
                    CommLog.error(e.getMessage(), e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送到数据到MQ
     *
     * @param topic 主题名
     * @param body  消息内容
     */
    public void sendGateway(String topic, Object body) {
        try {
            byte[] bytes;
            if (body != null) {
                bytes = (byte[]) body;
            } else {
                bytes = null;
            }
            Message msg = new Message(topic, "tagTest", "keyTest", bytes);
            mqProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                }

                @Override
                public void onException(Throwable e) {
                    CommLog.error(e.getMessage(), e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建主题
     *
     * @param topic
     */
    public void createTopic(String topic) {
        try {
            mqProducer.createTopic("TBW102", topic, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SingleCase {
        public static final MqProducerMgr INSTANCE = new MqProducerMgr();
    }

}
