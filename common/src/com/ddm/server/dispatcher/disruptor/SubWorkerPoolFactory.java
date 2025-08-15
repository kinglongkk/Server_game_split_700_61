package com.ddm.server.dispatcher.disruptor;

import com.ddm.server.common.utils.BasicThreadFactory;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Data
public class SubWorkerPoolFactory {

    private static class SingletonHolder {
        static final SubWorkerPoolFactory instance = new SubWorkerPoolFactory();
    }

    private SubWorkerPoolFactory() {
        initAndStart();
    }

    public static SubWorkerPoolFactory getInstance() {
        return SingletonHolder.instance;
    }

    private RingBuffer<MessageBuffer> ringBuffer;

    private SequenceBarrier sequenceBarrier;

    private WorkerPool<MessageBuffer> workerPool;

    /**
     * 默认消费者数量
     */
    private static final int DEFAULT_EVENT_HANDLE_SIZE = 2;

    /**
     * 消费者数量
     */
    private int eventHandleSize = DEFAULT_EVENT_HANDLE_SIZE;

    /**
     * 线程数
     */
    private int corePoolSize = DEFAULT_EVENT_HANDLE_SIZE;


    /**
     * 初始线程数和消费者数
     *
     * @return
     */
    public final void initCorePoolSize() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (availableProcessors <= DEFAULT_EVENT_HANDLE_SIZE) {
            // 线程数 <= 消费者数量 = 消费者数量;
            return;
        }
        // 设置实际消费者数
        this.setEventHandleSize(availableProcessors);
        // 设置实际线程数
        this.setCorePoolSize(availableProcessors + 1);
    }

    public void initAndStart() {
        // 初始线程数和消费者数
        this.initCorePoolSize();
        MessageEventHandler[] conusmers = new MessageEventHandler[getEventHandleSize()];
        for (int i = 0; i < conusmers.length; i++) {
            conusmers[i] = new MessageEventHandler();
        }
        this.initAndStart(ProducerType.MULTI, (int) Math.pow(2, 15), new BlockingWaitStrategy(), conusmers);
    }

    public void initAndStart(ProducerType type, int bufferSize, WaitStrategy waitStrategy, MessageEventHandler[] messageConsumers) {
        //1. 构建ringBuffer对象
        this.setRingBuffer(RingBuffer.create(type,
                new BufferEventFactory(),
                bufferSize,
                waitStrategy));
        //2.设置序号栅栏
        this.setSequenceBarrier(this.getRingBuffer().newBarrier());
        //3.设置工作池
        this.setWorkerPool(new WorkerPool<MessageBuffer>(this.getRingBuffer(), this.getSequenceBarrier(), new ErrorHandler(), messageConsumers));
        //4 把所构建的消费者置入池中
        //5 添加我们的sequences
        this.getRingBuffer().addGatingSequences(this.getWorkerPool().getWorkerSequences());
        //6 启动我们的工作池
        this.getWorkerPool().start(new ScheduledThreadPoolExecutor(getCorePoolSize(), new BasicThreadFactory.Builder().namingPattern("disruptor-pool-%d").build()));
    }


    public Producer publish() {
        return new Producer(this.getRingBuffer());
    }
}



