package com.ddm.server.common.redis;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

public class JsonUtil {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    public static String bean2Json(Object bean) throws Exception {
        if (bean == null) {
            return "";
        }
        String s = objectMapper.writeValueAsString(bean);
        return s;
    }

    public static Object json2Bean(String json, Class<?> cls) throws Exception {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return objectMapper.readValue(json, cls);
    }
}
