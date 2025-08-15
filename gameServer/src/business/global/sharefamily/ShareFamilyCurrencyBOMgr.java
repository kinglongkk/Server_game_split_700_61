package business.global.sharefamily;
import com.ddm.server.common.redis.RedisMap;
import com.google.gson.Gson;
import core.db.entity.clarkGame.FamilyCityCurrencyBO;
import core.ioc.ContainerMgr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author xsj
 * @date 2020/8/28 11:10
 * @description 城市钻石共享
 */
public class ShareFamilyCurrencyBOMgr {
    private static ShareFamilyCurrencyBOMgr instance = new ShareFamilyCurrencyBOMgr();
    //城市砖石缓存key
    private final String SHARE_FAMILY_CURRENCY_BO_KEY = "shareFamilyCurrencyBOKey";

    public static ShareFamilyCurrencyBOMgr getInstance() {
        return instance;
    }

    /**
     * 添加数据
     *
     * @param familyCityCurrencyBO
     */
    public void add(FamilyCityCurrencyBO familyCityCurrencyBO) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_FAMILY_CURRENCY_BO_KEY + String.valueOf(familyCityCurrencyBO.getFamilyId()));
        Gson gson = new Gson();
        ShareFamilyCityCurrencyBO shareFamilyCityCurrencyBO = gson.fromJson(gson.toJson(familyCityCurrencyBO), ShareFamilyCityCurrencyBO.class);
        redisMap.put(String.valueOf(familyCityCurrencyBO.getCityId()), gson.toJson(shareFamilyCityCurrencyBO));
    }

    /**
     * 删除数据
     *
     * @param familyCityCurrencyBO
     */
    public void remove(FamilyCityCurrencyBO familyCityCurrencyBO) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_FAMILY_CURRENCY_BO_KEY + String.valueOf(familyCityCurrencyBO.getFamilyId()));
        redisMap.remove(String.valueOf(familyCityCurrencyBO.getCityId()));
    }

    /**
     * 获取map对象
     *
     * @param pid
     * @return
     */
    public Map<Integer, FamilyCityCurrencyBO> getMap(Long pid) {
        Gson gson = new Gson();
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_FAMILY_CURRENCY_BO_KEY + String.valueOf(pid));
        Set<Map.Entry<String, String>> allSet = redisMap.entrySet();
        Map<Integer, FamilyCityCurrencyBO> familyCityCurrencyBOs = new HashMap<>(allSet.size());
        allSet.forEach(data -> {
            familyCityCurrencyBOs.put(Integer.parseInt(data.getKey()), gson.fromJson(data.getValue(), FamilyCityCurrencyBO.class));
        });
        return familyCityCurrencyBOs;
    }

    /**
     * 获取一个
     * @param pid
     * @param cityId
     * @return
     */
    public FamilyCityCurrencyBO get(Long pid, Integer cityId) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_FAMILY_CURRENCY_BO_KEY + String.valueOf(pid));
        String data = redisMap.get(String.valueOf(cityId));
        if (data != null) {
            return new Gson().fromJson(data, FamilyCityCurrencyBO.class);
        } else {
            return null;
        }
    }
}
