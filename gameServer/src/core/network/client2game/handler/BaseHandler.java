package core.network.client2game.handler;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.RequestHandler;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import com.ddm.server.common.CommLogD;

import core.network.client2game.ClientSession;

/**
 *
 */
public abstract class BaseHandler extends RequestHandler {
	public String getOpName() {
		return this.getClass().getSimpleName();
	}

	public BaseHandler() {
		super();
	}

	public BaseHandler(short opCode, String opName) {
		super(opCode, opName);
	}

	@Override
	public void handleMessage(final WebSocketRequest request, final String data) throws WSException, IOException {
		if (!(request.getSession() instanceof ClientSession)) {
			CommLogD.warn("{} not handled.", request.getHeader().event);
			return;
		}

		// 加解密处理
		try {
//			if (!GameServer.NOTLOG)
				CommLogD.info("request client sessonID：{}, Data：{},Interface：{}",request.getSession().getSessionId(), data, getOpName());
				handle(request, data);
		} catch (Throwable e) {
			CommLogD.error(this.getClass().getName() + " Exception: ", e);
			request.error(ErrorCode.NotAllow, e.toString());
		}
	}

	public abstract void handle(final WebSocketRequest request, final String message) throws IOException;
}
