package com.ddm.server.common.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.utils.JavaSerializeUtil;
import redis.clients.jedis.*;

public class RedisUtil {
	//大于这个时间毫秒
	private static final int LONG_TIME= 50;

	public static RedisSource getRedisSource() {
		return new RedisSource();
	}
	
	/**清空数据*/
	public static void flushAll() {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			jedis.flushAll();
		} catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**判断某个键是否存在*/
	public static boolean exists(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.exists(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取所有key*/
	public static Set<String> keys() {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.keys("*");
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取所有key*/
	public static Set<String> keys(String pattern) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.keys(pattern);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**设置键为key的过期时间为seconds秒*/
	public static boolean expire(String key, int seconds) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.expire(key, seconds) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取键为key数据项的剩余生存时间（秒）*/
	public static long ttl(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.ttl(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**移除键为key属性项的生存时间限制*/
	public static boolean persist(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.persist(key) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**查看键为key所对应value的数据类型*/
	public static String type(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.type(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**新增键值对*/
	public static String set(String key, String value) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.set(key, value);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	/**新增键值对*/
	public static String set(String key, String value, String nxxx, String expx, long time) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.set(key, value, nxxx, expx, time);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**如果key数据项已存在，则插入失败*/
	public static boolean setnx(String key, String value) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.setnx(key, value) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**增加数据项并设置有效时间*/
	public static String setex(String key, int seconds, String value) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.setex(key, seconds, value);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**删除键为key的数据项*/
	public static boolean del(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.del(key) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取键为key对应的value*/
	public static String get(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.get(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**增加多个键值对*/
	public static String mset(String ...keyAndValue) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.mset(keyAndValue);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取多个key对应value*/
	public static List<String> mget(String ...key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.mget(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**删除多个key对应数据项*/
	public static long del(String ...key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.del(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取key对应value并更新value*/
	public static String getSet(String key, String value) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.getSet(key, value);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	/**将key对应的value自加1*/
	public static Long incrLong(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.incr(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0L;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**将key对应的value自加1*/
	public static boolean incr(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.incr(key) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**将key对应的value自加n*/
	public static boolean incrBy(String key, int n) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.incrBy(key, n) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**将key对应的value自加n*/
	public static boolean incrByFloat(String key, float n) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.incrByFloat(key, n) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**将key对应的value自减1*/
	public static boolean decr(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.decr(key) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**将key对应的value自减n*/
	public static boolean decrBy(String key, int n) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.decrBy(key, n) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**
	 * map
	 */
	
	/**添加一个hash*/
	public static String hmset(String key, Map<String, String> map) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hmset(key, map);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=hmset", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**往hash插入一个元素（k-v）*/
	public static boolean hset(String key, String hkey, String hvalue) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hset(key, hkey, hvalue) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={} hkey={}", System.currentTimeMillis() - startTime, key, hkey);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	public static boolean hsetObject(String key, String hkey, Object hvalue){
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hset(key.getBytes(), hkey.getBytes(), JavaSerializeUtil.serialize(hvalue)) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={}, hkey={} method=hsetObject", System.currentTimeMillis() - startTime, key, hkey);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取hash所有（k-v）元素*/
	public static Map<String, String> hgetAll(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hgetAll(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={}, method=hgetAll", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	/**获取hash所有（k-v）元素*/
	public static List<Map.Entry<String, String>> hscan(String key, Integer cursor, Integer size) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			ScanParams scanParams=new ScanParams();
			scanParams.count(size);
			ScanResult<Map.Entry<String, String>> result = jedis.hscan(key,cursor.toString(),scanParams);
			List<Map.Entry<String, String>> list = result.getResult();
			return list;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={}, method=hscan", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	/**获取hash所有（k-v）元素*/
	public static Map hgetAllObject(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			Map<byte[], byte[]> map = RedisMgr.get().getConnection().hgetAll(key.getBytes());
			Map result = new HashMap();
			map.forEach((k, v) -> {
//            result.put(JsonUtil.parseObject(k, Object.class), JsonUtil.parseObject(v, Object.class));
				result.put(new String(k), JavaSerializeUtil.unSerialize(v));
			});

			return result;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={} mehtod=hgetAllObject", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取hash所有元素的key*/
	public static Set<String> hkeys(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hkeys(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=hkeys", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取hash所有元素的value*/
	public static List<String> hvals(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hvals(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=hvals", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**把hash中key对应元素val+=n*/
	public static boolean hincrBy(String key, String hkey, int n) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hincrBy(key, hkey, n) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**把hash中key对应元素val+=n*/
	public static boolean hincrByFloat(String key, String hkey, float n) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hincrByFloat(key, hkey, n) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**从hash删除一个元素*/
	public static boolean hdel(String key, String hkey) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hdel(key, hkey) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} hkey={} method=hdel", System.currentTimeMillis() - startTime, key, hkey);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	/**从hash删除一个元素*/
	public static boolean hdelObject(String key, String hkey) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hdel(key.getBytes(), hkey.getBytes()) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} hkey={} method=hdelObject", System.currentTimeMillis() - startTime, key, hkey);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**从hash删除多个元素*/
	public static long hdel(String key, String ...hkey) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hdel(key, hkey);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	/**获取hash中元素个数*/
	public static long hlenObject(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hlen(key.getBytes());
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=hlenObject", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取hash中元素个数*/
	public static long hlen(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hlen(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={}, method=hlen", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**判断hash是否存在hkey对应元素*/
	public static boolean hexists(String key, String hkey) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hexists(key, hkey);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={}, hkey={} method=hexists", System.currentTimeMillis() - startTime, key, hkey);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取hash中多个元素value*/
	public static List<String> hmget(String key, String ...hkey) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hmget(key, hkey);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取hash中一个元素value*/
	public static String hget(String key, String hkey) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.hget(key, hkey);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={} hkey={} method=hget", System.currentTimeMillis() - startTime, key, hkey);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	/**获取hash中一个元素value*/
	public static <T> T hgetObject(String key, String hkey) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			byte[] data =  RedisMgr.get().getConnection().hget(key.getBytes(), hkey.getBytes());
			if(data == null){
				return null;
			} else {
				return (T) JavaSerializeUtil.unSerialize(data);
			}
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={} hkey={} method=hgetObject", System.currentTimeMillis() - startTime, key, hkey);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**添加一个list*/
	public static boolean lpush(String key, String ...val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.lpush(key, val) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**往key对应list左插入一个元素val*/
	public static boolean lpush(String key, String val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.lpush(key, val) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取key对应list期间[i,j]的元素*/
	public static List<String> lrange(String key, int startIndex, int endIndex) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.lrange(key, startIndex, endIndex);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**删除指定元素val个数num*/
	public static long lrem(String key, int num, String val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.lrem(key, num, val);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**删除list区间[i,j]之外的元素*/
	public static String ltrim(String key, int startIndex, int endIndex) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.ltrim(key, startIndex, endIndex);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**删除index的元素*/
	public static void ldel(String key, int index) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			Transaction multi = jedis.multi();
			multi.lset(key, index, "__deleted__");
			multi.lrem(key, 1, "__deleted__");
			multi.exec();
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**key对应list左出栈一个元素*/
	public static String lpop(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.lpop(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**key对应list右插入一个元素val*/
	public static boolean rpush(String key, String val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.rpush(key, val) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**key对应list右插入多个元素val*/
	public static long rpush(String key, String ...val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.rpush(key, val);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**key对应list右出栈一个元素*/
	public static String rpop(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.rpop(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**修改key对应list指定下标index的元素*/
	public static String lset(String key, int index, String val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.lset(key, index, val);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取key对应list的长度*/
	public static long llen(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.llen(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取key对应list下标为index的元素*/
	public static String lindex(String key, int index) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.lindex(key, index);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**添加多个val*/
	public static long sadd(String key, String ...val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.sadd(key, val);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=sadd", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取key对应set的所有元素*/
	public static Set<String> smembers(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.smembers(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=smembers", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}

	/**获取key对应set的所有元素*/
	public static boolean sismember(String key, String val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.sismember(key,val);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={}, method=sismember", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**删除一个值为val的元素*/
	public static boolean srem(String key, String val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.srem(key, val) > 0;
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return false;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={}, method=srem", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**删除值为val1,val2的元素*/
	public static long srem(String key, String ...val) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.srem(key, val);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms, key={}, method=srem", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**随机出栈set里的一个元素*/
	public static String spop(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.spop(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=spop", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**获取set中元素个数*/
	public static long scard(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.scard(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return 0;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms key={} method=scard", System.currentTimeMillis() - startTime, key);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**排序*/
	public static List<String> sort(String key) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.sort(key);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
	
	/**排序*/
	public static List<String> sort(String key, SortingParams sortingParameters) {
		long startTime = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			jedis = RedisMgr.get().getConnection();
			return jedis.sort(key, sortingParameters);
		}  catch (Exception e) {
			e.printStackTrace();
			CommLogD.error(e.getMessage(), e);
			return null;
		} finally {
			if (System.currentTimeMillis() - startTime > LONG_TIME) {
				CommLogD.error("redis time {} ms", System.currentTimeMillis() - startTime);
			}
			RedisMgr.get().discardConnectionFromRedis(jedis);
		}
	}
}
