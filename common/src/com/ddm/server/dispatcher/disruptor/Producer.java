package com.ddm.server.dispatcher.disruptor;

import BaseCommon.CommLog;
import com.ddm.server.dispatcher.executor.BaseExecutor;
import com.lmax.disruptor.RingBuffer;

public class Producer {

    private final RingBuffer<MessageBuffer> ringBuffer;

    public Producer(RingBuffer<MessageBuffer> ringBuffer){
        this.ringBuffer=ringBuffer;
    }
    public void publish(BaseExecutor executor) {
        final long seq = ringBuffer.next();
        try {
            MessageBuffer buffer = ringBuffer.get(seq);
            buffer.setExecutor(executor);
        } catch (Exception e) {
            CommLog.error("[Producer]: error:{}", e.getMessage(), e);
        } finally {
            ringBuffer.publish(seq);
        }
    }
}
