package com.ddm.server.common.utils;

import BaseCommon.CommLog;
import net.sf.cglib.beans.BeanCopier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xsj
 * @date 2020/8/24 13:48
 * @description 对象属性拷贝工具
 */
public class BeanUtils {
    private static Map<String, BeanCopier> map = new HashMap<>();

    /**
     * 单纯属性拷贝
     *
     * @param dest
     * @param orig
     */
    public static void copyProperties(Object dest, Object orig) {
        if (orig != null) {
            BeanCopier beanCopier = BeanCopier.create(orig.getClass(), dest.getClass(), false);
            beanCopier.copy(orig, dest, null);
        }
    }

    /**
     * 对象复制
     *
     * @param obj1   被复制对象，为空会抛出异常
     * @param classz 复制类型
     * @param <T>
     * @return
     */
    public static <T> T copyObject(Object obj1, Class<T> classz) {
        if (obj1 == null || classz == null) {
            throw new IllegalArgumentException("复制对象或者被复制类型为空!");
        }
        Object obj2 = null;
        try {
            obj2 = classz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            CommLog.error(e.getMessage(), e);
        }
        String name = getClassName(obj1.getClass(), classz);
        BeanCopier beanCopier;
        if (map.containsKey(name)) {
            beanCopier = map.get(name);
        } else {
            beanCopier = BeanCopier.create(obj1.getClass(), classz, false);
            map.put(name, beanCopier);
        }
        beanCopier.copy(obj1, obj2, null);
        return (T) obj2;
    }

    /**
     * 复制队列
     *
     * @param list   被复制队列
     * @param classz 复制类型
     * @param <T>
     * @return
     */
    public static <T> List<T> copyList(List<?> list, Class<T> classz) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("被复制的队列为空!");
        }
        List<Object> resultList = new LinkedList<>();
        for (Object obj1 : list) {
            resultList.add(copyObject(obj1, classz));
        }
        return (List<T>) resultList;
    }

    private static String getClassName(Class<?> class1, Class<?> class2) {
        return class1.getName() + class2.getName();
    }
}
