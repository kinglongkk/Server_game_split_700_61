package com.ddm.server.websocket.handler.requset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.def.TerminalType;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.IBaseHandler;
import com.ddm.server.websocket.handler.MessageDispatcher;
import com.ddm.server.websocket.handler.MessageHeader;
import com.ddm.server.websocket.server.ServerSession;

import com.ddm.server.common.CommLogD;


public abstract class RequestDispatcher<Session extends ServerSession> extends MessageDispatcher<Session> {
    protected final Map<String, RequestHandler> _requestHandlers;

    public RequestDispatcher(TerminalType thisServerType, int thisServerId) {
        super(thisServerType, thisServerId);
        this._requestHandlers = new HashMap<>();
    }

    public RequestDispatcher(TerminalType serverType, int serverId, Map<String, RequestHandler> requestHandlers) {
        super(serverType, serverId);
        this._requestHandlers = requestHandlers;
    }

    @Override
    public void handle(Session session, MessageHeader header, String data) {
        try {
            RequestHandler handler = _requestHandlers.get(header.event);
            if (handler == null) {
                session.sendError(header, ErrorCode.Request_NotFoundHandler, "协议[" + header.event + "]未找到处理器");
                return;
            }
            handler.handleMessage(new WebSocketRequest(session, header), data);
        } catch (WSException e) {
            CommLogD.warn("handle [0x{}] request failed, reason: {}", header.event, e.getMessage());
            session.sendError(header, e.getErrorCode(), e.getMessage());
            e.callback();
        } catch (IOException e) {
            CommLogD.error("handle [0x{}] request parse message error, detail:{}", header.event, e.getMessage(), e);
            session.sendError(header, ErrorCode.Unknown, "proto trans error");
        } catch (Throwable e) {
            CommLogD.error("handle [0x{}] request failed, reason: {}", header.event, e.getMessage(), e);
            session.sendError(header, ErrorCode.Unknown, "internal error");
        }
    }

    @Override
    public void addHandler(IBaseHandler handler) {
        _requestHandlers.put(handler.getEvent(), (RequestHandler) handler);
    }

    @Override
    public RequestHandler getHandler(String event) {
        return _requestHandlers.get(event);
    }
}
