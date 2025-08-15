package com.ddm.server.http.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Http异步请求 动态超时
 *
 * 
 */
public class HttpAsyncTimeoutClient {
	static CloseableHttpAsyncClient httpclient = null;

	public static void init(int socketTimeout, int connectTimeout) {
		if (httpclient == null) {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout).build();
			httpclient = HttpAsyncClients.custom()
					.setDefaultRequestConfig(requestConfig).build();
			httpclient.start();
		}
	}

	public static void start(HttpRequestBase request, int socketTimeout,
			int connectTimeout, FutureCallback<HttpResponse> response) {
		init(socketTimeout, connectTimeout);
		httpclient.execute(request, response);
	}

	public static void startHttpGet(String request, int socketTimeout,
			int connectTimeout, FutureCallback<HttpResponse> response) {
		init(socketTimeout, connectTimeout);
		httpclient.execute(new HttpGet(request), response);
	}

	public static void startHttpGet(String request, int socketTimeout,
			int connectTimeout, final IResponseHandler response) {
		init(socketTimeout, connectTimeout);
		httpclient.execute(new HttpGet(request), response);
	}

	public static void startHttpPost(final HttpPost httpRequest,
			int socketTimeout, int connectTimeout,
			final IResponseHandler response) {
		init(socketTimeout, connectTimeout);
		httpclient.execute(httpRequest, response);
	}
}
