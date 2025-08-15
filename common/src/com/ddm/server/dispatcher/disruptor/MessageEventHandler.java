//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package com.ddm.server.dispatcher.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * @author 0x737263
 */
public class MessageEventHandler implements EventHandler<MessageBuffer>,WorkHandler<MessageBuffer> {

    @Override
    public void onEvent(MessageBuffer buffer) throws Exception {
        try {
            buffer.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            buffer.clear();
        }
    }

    @Override
    public void onEvent(MessageBuffer buffer, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(buffer);
    }
}
