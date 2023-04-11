package com.UDT.outbound;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


public class CacheConfig {
   /* @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("addresses");
    }

    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(asList(USERS_CACHE, TRANSACTIONS_CACHE));
    }*/

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache booksCache = new ConcurrentMapCache("token");
        cacheManager.getCache(String.valueOf(booksCache));
        System.out.println(booksCache);
        return cacheManager;
    }

}
