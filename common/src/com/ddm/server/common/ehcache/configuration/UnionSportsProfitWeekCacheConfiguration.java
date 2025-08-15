package com.ddm.server.common.ehcache.configuration;

import com.ddm.server.common.ehcache.BaseCacheConfiguration;
import com.ddm.server.common.ehcache.EhCacheConfiguration;
import com.ddm.server.common.ehcache.EhcacheInit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import java.time.Duration;

/**
 * 联赛最近一周收益缓存
 */
@Data
@NoArgsConstructor
@EhCacheConfiguration
public class UnionSportsProfitWeekCacheConfiguration extends BaseCacheConfiguration {
    /**
     * 堆缓存大小 单位KB
     */
    private final int HEAP_CACHE_SIZE = 50;

    /**
     * 堆外缓存大小 单位MB
     */
    private final int OFF_HEAP_CACHE_SIZE = 10;

    /**
     * 堆可缓存的最大对象大小 单位MB
     */
    private final long HEAP_MAX_OBJECT_SIZE = 1L;

    /**
     * 统计对象大小时对象图遍历深度
     */
    private final long HEAP_MAX_OBJECT_GRAPH = 1000L;

    /**
     * 缓存配置名称
     */
    private final String name = getClass().getName();
    /**
     * ehcache缓存超时时间 单位秒
     */
    private final int EHCACHE_TTL = 300;

    /**
     * 初始化
     */
    @EhcacheInit
    @Override
    public void init(CacheManager cacheManager) {
        // 缓存对象
        this.setCache(cacheManager.createCache(this.getName(), getConfiguration()));
        this.setCacheManager(cacheManager);
    }


    /**
     * 缓存配置
     *
     * @return
     */
    @Override
    public CacheConfiguration getConfiguration() {
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, byte[].class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                // 堆内缓存大小
                                .heap(HEAP_CACHE_SIZE, MemoryUnit.KB)
                                // 堆外缓存大小
                                .offheap(OFF_HEAP_CACHE_SIZE, MemoryUnit.MB))
                // 缓存超时时间  timeToIdleSeconds：单位是秒，表示一个元素在不被请求的情况下允许在缓存中存在的最长时间。默认值是0，表示不限制。
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(EHCACHE_TTL)))
                // 统计对象大小时对象图遍历深度
                .withSizeOfMaxObjectGraph(HEAP_MAX_OBJECT_GRAPH)
                // 可缓存的最大对象大小
                .withSizeOfMaxObjectSize(HEAP_MAX_OBJECT_SIZE, MemoryUnit.KB)
                .build();
    }

    @Override
    public void dispose() {
        this.getCacheManager().removeCache(this.getName());
    }


}
