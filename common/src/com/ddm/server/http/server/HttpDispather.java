package com.ddm.server.http.server;

import java.lang.reflect.Method;
import java.util.*;

import BaseCommon.CommLog;
import com.ddm.server.common.Config;
import com.ddm.server.common.utils.IpUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.nettosphere.Handler;

import com.ddm.server.common.CommLogD;
import com.ddm.server.http.annotation.RequestMapping;

import BaseCommon.CommClass;

public class HttpDispather implements Handler {

    private Map<HttpMethod, MethodAdapater> methodAdapaters = new HashMap<>();

    private Set<String> allowRequestedIP = new HashSet<>();

    public void init(String pack) throws Exception {
        this.allowRequestedIP = new Gson().fromJson(Config.getAllowRequestedIP(),Set.class);
        Set<Class<?>> dealers = CommClass.getClasses(pack);
        for (Class<?> cs : dealers) {
            Object instance = null;
            for (Method method : cs.getMethods()) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                if (mapping == null) {
                    continue;
                }
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 2) {
                    throw new IllegalArgumentException("[" + method.getName() + "]不是固定2个参数");
                }
                if (params[0] != HttpRequest.class) {
                    throw new IllegalArgumentException("[" + method.getName() + "]第一个参数不是HttpRequest");
                }
                if (params[1] != HttpResponse.class) {
                    throw new IllegalArgumentException("[" + method.getName() + "]第二个参数不是HttpResponse");
                }
                if (instance == null) {
                    instance = CommClass.forName(cs.getName()).newInstance();
                }
                for (HttpMethod httpMethod : mapping.method()) {
                    if (methodAdapaters.get(httpMethod) == null) {
                        methodAdapaters.put(httpMethod, new MethodAdapater());
                    }
                    methodAdapaters.get(httpMethod).addAdapter(mapping.uri(), new HttpAdaperter(instance, method));
                }
            }
        }
    }


    @Override
    public void handle(AtmosphereResource resource) {
        AtmosphereRequest atmosphereRequest = resource.getRequest();
        AtmosphereResponse atmosphereResponse = resource.getResponse();
        HttpResponse response = new HttpResponse(resource);
        try {
            atmosphereRequest.setCharacterEncoding("UTF-8");
            atmosphereResponse.setContentType("text/json;charset=UTF-8");
            // 请求类型
            MethodAdapater methodAdapater = methodAdapaters.get(HttpMethod.nameOf(atmosphereRequest.getMethod().trim().toUpperCase()));
            if (this.checkAllowRequestedIP(atmosphereRequest)) {
                if (Objects.nonNull(methodAdapater) && methodAdapater.containsKey(atmosphereRequest.getRequestURI())) {
                    methodAdapater.getAdaperter(atmosphereRequest.getRequestURI()).invoke(new HttpRequest(resource), response);
                } else {
                    response.response(404, "File Not Found");
                }
            } else {
                response.response(405, "File Not Found!");
            }
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof RequestException) {
                RequestException re = (RequestException) cause;
                response.error(re.getCode(), re.getMessage());
            } else {
                response.error(300001, "服务器发生未知错误，错误信息：%s", e.getMessage());
            }
            CommLogD.error("Http 服务器发生未知错误，错误信息：{}", e.getMessage());
        }
    }

    public boolean checkAllowRequestedIP(AtmosphereRequest atmosphereRequest) {
        String ipStr = IpUtils.getIpAdrress(atmosphereRequest);
        if (StringUtils.isEmpty(ipStr)) {
            return false;
        }
        return this.allowRequestedIP.contains(ipStr);
    }
}
