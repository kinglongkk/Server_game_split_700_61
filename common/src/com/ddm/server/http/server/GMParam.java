package com.ddm.server.http.server;

import java.net.URLEncoder;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import com.ddm.server.common.utils.secure.MD5;
import com.google.gson.JsonObject;

public class GMParam extends TreeMap<String, Object> {

    private static final long serialVersionUID = 4770492078712306761L;

    private String key = null;

    public GMParam() {
        this.key = HttpUtils.SIGN_KEY;
    }

    public GMParam(String key) {
        this.key = key;
    }

    public HttpEntity toEntity() throws Exception {
        JsonObject json = new JsonObject();
        StringBuilder signsrc = new StringBuilder();
        for (java.util.Map.Entry<String, Object> pair : this.entrySet()) {
            String value = pair.getValue().toString();
            json.addProperty(pair.getKey(), value);
            value = URLEncoder.encode(value, "utf-8");
            signsrc.append(pair.getKey()).append("=").append(value).append("&");
        }
        signsrc = signsrc.append(key);
        json.addProperty("sign", MD5.md5(signsrc.toString()));
        return new StringEntity(json.toString());
    }

    public String toUrlParam() throws Exception {
        StringBuilder params = new StringBuilder("?");
        StringBuilder signsrc = new StringBuilder();
        for (java.util.Map.Entry<String, Object> pair : this.entrySet()) {
            String value = pair.getValue().toString();
            value = URLEncoder.encode(value, "utf-8");
            signsrc.append(pair.getKey()).append("=").append(value).append("&");
            params.append(pair.getKey()).append("=").append(value).append("&");
        }
        signsrc = signsrc.append(key);
        params.append("sign").append("=").append(MD5.md5(signsrc.toString()));
        return params.toString();
    }
}
