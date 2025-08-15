package com.ddm.server.http.server;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.atmosphere.cpr.AtmosphereResource;

import com.ddm.server.common.CommLogD;


public class HttpRequest {

    private AtmosphereResource httpExchange;
    private String requestBody = "";

    public HttpRequest(AtmosphereResource httpExchange) {
        this.httpExchange = httpExchange;
        this.initRequestBody();
    }




    private void initRequestBody() {
        try (InputStream inputStream = httpExchange.getRequest().getInputStream()) {
            char[] cbuf = new char[inputStream.available()];
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            inputStreamReader.read(cbuf);
            inputStream.close();
            requestBody = String.valueOf(cbuf).trim();
        } catch (Exception e) {
            CommLogD.error("[HttpRequest]解析http协议body发生错误");
        }
    }

    public String getRequestBody() {
        return requestBody;
    }
}
