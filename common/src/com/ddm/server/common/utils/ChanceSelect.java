package com.ddm.server.common.utils;

import java.util.Map;
import java.util.Random;

/**
 * 概率的选择
 * @author Huaxing
 *
 */
public class ChanceSelect {
    public  final static <T> T ChanceSelect(Map<T, Integer> keyChanceMap) {
        if (keyChanceMap == null || keyChanceMap.size() == 0) {
            return null;
        }
        Integer sum = 0;
        for (Integer value : keyChanceMap.values()) {
            sum += value;
        }
        // 从1开始
        Integer rand = new Random().nextInt(sum) + 1;

        for (Map.Entry<T, Integer> entry : keyChanceMap.entrySet()) {
            rand -= entry.getValue();
            // 选中
            if (rand <= 0) {
                return entry.getKey();
            }
        }
        return null;
    }
    
}