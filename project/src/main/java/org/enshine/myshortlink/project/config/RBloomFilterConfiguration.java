package org.enshine.myshortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 短链接uri 布隆过滤器
     */
    @Bean
    public RBloomFilter<String> saveLinkCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("saveLinkCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000, 0.01);
        return cachePenetrationBloomFilter;
    }
}