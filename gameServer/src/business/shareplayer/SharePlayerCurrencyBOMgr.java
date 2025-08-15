package business.shareplayer;

import com.ddm.server.common.redis.RedisMap;
import com.google.gson.Gson;
import core.db.entity.clarkGame.PlayerCityCurrencyBO;
import core.ioc.ContainerMgr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author xsj
 * @date 2020/8/28 11:10
 * @description 城市钻石共享
 */
public class SharePlayerCurrencyBOMgr {
    private static SharePlayerCurrencyBOMgr instance = new SharePlayerCurrencyBOMgr();
    //城市砖石缓存key
    private final String SHARE_PLAYER_CURRENCY_BO_KEY = "sharePlayerCurrencyBOKey";

    public static SharePlayerCurrencyBOMgr getInstance() {
        return instance;
    }

    /**
     * 添加数据
     *
     * @param playerCityCurrencyBO
     */
    public void add(PlayerCityCurrencyBO playerCityCurrencyBO) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_PLAYER_CURRENCY_BO_KEY + String.valueOf(playerCityCurrencyBO.getPid()));
        Gson gson = new Gson();
        SharePlayerCityCurrencyBO sharePlayerCityCurrencyBO = gson.fromJson(gson.toJson(playerCityCurrencyBO), SharePlayerCityCurrencyBO.class);
        redisMap.put(String.valueOf(playerCityCurrencyBO.getCityId()), gson.toJson(sharePlayerCityCurrencyBO));
    }

    /**
     * 删除数据
     *
     * @param playerCityCurrencyBO
     */
    public void remove(PlayerCityCurrencyBO playerCityCurrencyBO) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_PLAYER_CURRENCY_BO_KEY + String.valueOf(playerCityCurrencyBO.getPid()));
        redisMap.remove(String.valueOf(playerCityCurrencyBO.getCityId()));
    }

    /**
     * 获取map对象
     *
     * @param pid
     * @return
     */
    public Map<Integer, PlayerCityCurrencyBO> getMap(Long pid) {
        Gson gson = new Gson();
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_PLAYER_CURRENCY_BO_KEY + String.valueOf(pid));
        Set<Map.Entry<String, String>> allSet = redisMap.entrySet();
        Map<Integer, PlayerCityCurrencyBO> playerCityCurrencyBOs = new HashMap<>(allSet.size());
        allSet.forEach(data -> {
            playerCityCurrencyBOs.put(Integer.parseInt(data.getKey()), gson.fromJson(data.getValue(), PlayerCityCurrencyBO.class));
        });
        return playerCityCurrencyBOs;
    }

    /**
     * 获取一个
     * @param pid
     * @param cityId
     * @return
     */
    public PlayerCityCurrencyBO get(Long pid, Integer cityId) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_PLAYER_CURRENCY_BO_KEY + String.valueOf(pid));
        String data = redisMap.get(String.valueOf(cityId));
        if (data != null) {
            return new Gson().fromJson(data, PlayerCityCurrencyBO.class);
        } else {
            return null;
        }
    }
}
