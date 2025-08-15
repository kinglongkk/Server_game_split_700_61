package com.ddm.server.http.server;

import com.ddm.server.common.CommLogD;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;

public class HttpResponse {
    private AtmosphereResource httpExchange;

    public HttpResponse(AtmosphereResource httpExchange) {
        this.httpExchange = httpExchange;
    }

    public void response(String result) {
        response(200, result);
    }

    public void response(int code, String result) {
        try {
            AtmosphereResponse response = httpExchange.getResponse();
            // 设置响应头属性及响应信息的长度
            response.setStatus(code);
            response.write(result.getBytes()).flushBuffer();
            httpExchange.close();
        } catch (Exception e) {
            CommLogD.error("回写Http数据response时发生错误", e);
        }
    }


    public void error(int code, String format, Object... param) {
        String msg = String.format(format, param);
        String rep = String.format("{\"Code\":%d,\"Msg\":\"%s\"}", code, this.encodeString(msg));
        this.response(rep);
        CommLogD.error("{}请求处理失败,错误码:{},msg:{}", httpExchange.getRequest().getRequestURI(), code, msg);
    }

    private String encodeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default: {
                    sb.append(ch);
                    break;
                }
            }
        }
        return sb.toString();
    }
}
