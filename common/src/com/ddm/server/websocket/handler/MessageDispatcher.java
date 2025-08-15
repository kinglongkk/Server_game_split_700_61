package com.ddm.server.websocket.handler;

import com.ddm.server.dispatcher.DispatchHandle;
import com.ddm.server.dispatcher.disruptor.Producer;
import com.ddm.server.dispatcher.disruptor.RingBufferWorkerPoolFactory;
import com.ddm.server.websocket.def.TerminalType;
import com.ddm.server.websocket.server.ServerSession;

import com.ddm.server.common.CommLogD;

import BaseTask.SyncTask.SyncTaskManager;

public abstract class MessageDispatcher<Session extends ServerSession> {

    protected int thisServerId;
    protected int thisServerType;

    public MessageDispatcher(TerminalType thisServerType, int thisServerId) {
        this.thisServerType = thisServerType.ordinal();
        this.thisServerId = thisServerId;
    }

    public int dispatch(final Session session, MessageHeader header, String message) {
        if (header == null) {
            CommLogD.error("[MessageHandler]解析协议头部信息时发生错误,返回头部信息为空。");
            return -1;
        }
        // TODO 2020/1/10 Disruptor 生产者生产速度低于消费者消费速度会导致死锁。
        RingBufferWorkerPoolFactory.getInstance().publish().publish(new DispatchHandle(this,session,header,message));
        return 0;
    }

    public abstract void handle(Session session, MessageHeader header, String body);

    public abstract void forward(Session session, MessageHeader header, String body);

    public abstract void addHandler(IBaseHandler handler);

    public abstract IBaseHandler getHandler(String event);
}
