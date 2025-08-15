package com.ddm.server.dispatcher;

import java.util.HashMap;
import java.util.Map;

import com.ddm.server.dispatcher.executor.BaseExecutor;
import com.ddm.server.http.server.HttpMethod;
import com.ddm.server.http.server.MethodAdapater;
import com.sun.net.httpserver.HttpExchange;

public class HttpHandle implements BaseExecutor {
	private HttpExchange exchange;
	private Map<HttpMethod, MethodAdapater> methodAdapaters = new HashMap<>();

	public HttpHandle(HttpExchange exchange, Map<HttpMethod, MethodAdapater> methodAdapaters) {
		super();
		this.exchange = exchange;
		this.methodAdapaters = methodAdapaters;
	}

	@Override
	public void invoke() {
//		HttpResponse response = new HttpResponse(exchange);
//
//		try {
//			HttpMethod method = null;
//			try {
//				String methodname = exchange.getRequestMethod().trim().toUpperCase();
//				method = HttpMethod.valueOf(methodname);
//
//			} catch (Exception e) {
//				method = HttpMethod.GET;
//			}
//			MethodAdapater methodAdapater = methodAdapaters.get(method);
//			if (methodAdapater == null) {
//				response.response(404, "File Not Found");
//				return;
//			}
//			HttpAdaperter adaperter = methodAdapater.getAdaperter(exchange.getRequestURI().getPath());
//			if (adaperter == null) {
//
//				response.response(404, "File Not Found");
//				return;
//			}
//			HttpRequest request = new HttpRequest(exchange);
//			adaperter.invoke(request, response);
//		} catch (Exception e) {
//			Throwable cause = e.getCause();
//			if (cause != null && cause instanceof RequestException) {
//				RequestException re = (RequestException) cause;
//				response.error(re.getCode(), re.getMessage());
//			} else {
//				response.error(300001, "服务器发生未知错误，错误信息：%s", e.getMessage());
//			}
//			CommLogD.error("Http 服务器发生未知错误，错误信息：{}", e.getMessage());
//		}

	}

	@Override
	public int threadId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
