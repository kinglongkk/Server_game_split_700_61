package com.ddm.server.common.rocketmq;

import com.ddm.server.annotation.Consumer;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.google.gson.Gson;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author : xushaojun
 * create at:  2020-8-12  16:08
 * @description: RocketMQ消费者
 */
public class MqConsumerMgr {
    private Map<String, MqConsumerHandler> consumerHandlers = new HashMap<>();
    private Boolean isMqStart;
    private String namesrvAddr;
    private String groupName;
    // 实例化消费者
    private DefaultMQPushConsumer consumer;

    private MqConsumerMgr() {

    }

    public static MqConsumerMgr get() {
        return MqConsumerMgr.SingleCase.INSTANCE;
    }

    public void loadConfig(String path, String basePackages) throws Exception {
        loadConfig(new FileInputStream(path), basePackages);
    }

    private void loadConfig(InputStream in, String basePackages) throws Exception {
        Properties pro = new Properties();
        pro.load(in);
        this.isMqStart = Boolean.parseBoolean(pro.getProperty("mq.start.consumer"));
        this.namesrvAddr = pro.getProperty("mq.namesrv.addr");
        this.groupName = pro.getProperty("mq.group.name");
        if (isMqStart) {
            init(basePackages);
        }
    }

    /**
     * 获取所有注解的方法
     *
     * @return
     */
    public void init(String basePackages) {
        consumer = new DefaultMQPushConsumer(groupName);
        Set<Class<?>> clazzs = ClassFind.getClasses(basePackages);
        for (Class<?> clazz : clazzs) {
            try {
                if (clazz.isAnnotationPresent(Consumer.class)) {
                    Class<MqConsumerHandler> handlerClass = (Class) clazz;
                    Consumer consumer = clazz.getAnnotation(Consumer.class);
                    String topic = consumer.topic() + (consumer.id() == -1 ? "" : consumer.id() + "");
                    topic = Config.LOCAL_SERVER.equals(topic) ? Config.getLocalServerTopic():topic;
                    consumerHandlers.put(topic, handlerClass.newInstance());
                    //创建主题
                    MqProducerMgr.get().createTopic(topic);
                }
            } catch (Exception e) {
                e.printStackTrace();
                CommLogD.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 启动MQ订阅监听
     */
    public void start() {
        if (isMqStart) {
            // 设置NameServer的地址
            consumer.setNamesrvAddr(namesrvAddr);
            //设置广播消费
            consumer.setMessageModel(MessageModel.BROADCASTING);
            //设置集群消费
//        consumer.setMessageModel(MessageModel.CLUSTERING);
            //CONSUME_FROM_LAST_OFFSET 默认策略，从该队列最尾开始消费，即跳过历史消息
            //CONSUME_FROM_FIRST_OFFSET 从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
            //CONSUME_FROM_TIMESTAMP 从某个时间点开始消费，和setConsumeTimestamp()配合使用，默认是半个小时以前
//            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
            consumerHandlers.forEach((topic, handler) -> {
                try {
                    consumer.subscribe(topic, "*");
                } catch (MQClientException e) {
                    e.printStackTrace();
                    CommLogD.error(e.getMessage(), e);
                }
            });
            Gson gson = new Gson();
            // 注册回调实现类来处理从broker拉取回来的消息
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    try {
                        MessageExt messageExt = msgs.get(0);
                        //服务器启动之前的消息不消费
                        if(Config.getServerStartTime() < messageExt.getBornTimestamp()) {
                            String topic = messageExt.getTopic();
                            if (Config.getLocalServerTopic().equals(topic) || Config.REGISTRY_NOTIFY.equals(topic) || Config.SERVER_NOTIFY.equals(topic)) {
                                consumerHandlers.get(topic).action(messageExt.getBody());
                            } else {
                                String body = new String(messageExt.getBody(), "UTF-8");
                                MqAbsBo mqAbsBo = gson.fromJson(body, MqAbsBo.class);
                                Object requestBody = gson.fromJson(body, Class.forName(mqAbsBo.getClazzName()));
                                consumerHandlers.get(topic).action(requestBody);
                            }
                        } else {
                            CommLogD.warn("no consume rocketMq msgId={}, bornTimes={}, starTimes={}", messageExt.getMsgId(), messageExt.getBornTimestamp(), Config.getServerStartTime());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommLogD.error(e.getMessage(), e);
                    }
                    // 标记该消息已经被成功消费
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            // 启动消费者实例
            try {
                consumer.start();
            } catch (MQClientException e) {
                e.printStackTrace();
                CommLogD.error(e.getMessage(), e);
            }
        }
    }

    public void shutdown() {
        if (isMqStart) {
            consumer.shutdown();
        }
    }

    private static class SingleCase {
        public static final MqConsumerMgr INSTANCE = new MqConsumerMgr();
    }
}
