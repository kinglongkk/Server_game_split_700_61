package business.global.room.sharekey;

import BaseCommon.CommLog;
import BaseThread.BaseMutexManager;
import com.ddm.server.common.redis.DistributedRedisLock;
import com.ddm.server.common.redis.RedisSet;
import core.ioc.ContainerMgr;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author xsj
 * @date 2020/8/11 15:48
 * @description 共享房间号管理类
 */
public class ShareRoomKeyMgr {
    //使用过的key
    private static final String SHARE_ROOM_NO_KEY_USE = "shareRoomNoKey_use";
    //未使用的key
    private static final String SHARE_ROOM_NO_KEY_NO = "shareRoomNoKey_no";
    private static ShareRoomKeyMgr instance = new ShareRoomKeyMgr();
    private final BaseMutexManager lock = new BaseMutexManager();

    public static ShareRoomKeyMgr getInstance() {
        return instance;
    }

    public final void init() {
        initKey();
    }

    /**
     * 初始化添加KEy
     */
    private void initKey() {
        long start = System.currentTimeMillis();
        RedisSet redisSetNo = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_NO);
        RedisSet redisSetUse = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_USE);
        if (redisSetUse.size() == 0) {
            Set<String> newSet = new HashSet<>(899999);
            for (long i = 100000; i < 1000000; i++) {
                String key = String.valueOf(i);
                newSet.add(key);
            }
            redisSetNo.addAll(newSet);
            CommLog.info("初始化房间key耗时:{}", System.currentTimeMillis() - start);
        }
    }

    /**
     * 随机获取一个未使用的key并且设置到使用队列里面
     *
     * @return
     */
    public String getNoUseKey() {
        RedisSet redisSetNo = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_NO);
        String key = redisSetNo.getRandom();
        RedisSet redisSetUse = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_USE);
        redisSetUse.add(key);
        return key == null ? "" : key;
    }

    /**
     * 随机获取一个未使用的key并且设置到使用队列里面
     *
     * @return
     */
    public void giveBackKey(String key) {
//        lock.lock();
//        try {
            RedisSet redisSetNo = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_NO);
            redisSetNo.add(key);
            RedisSet redisSetUse = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_USE);
            redisSetUse.remove(key);
//        }finally {
//            lock.unlock();
//        }
    }

    /**
     * 是否使用
     *
     * @param key 房间key
     * @return T:没使用，F，使用中
     */
    public boolean isNotExistUse(String key) {
        RedisSet redisSetNo = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_NO);
        return redisSetNo.contains(key);
    }
    /**
     * 剩余没使用的房间号
     */
    public int noUseKeySize() {
        RedisSet redisSetNo = ContainerMgr.get().getRedis().getSet(SHARE_ROOM_NO_KEY_NO);
        return redisSetNo.size();
    }

}
