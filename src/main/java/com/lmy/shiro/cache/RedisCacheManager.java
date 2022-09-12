package com.lmy.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/*
准备再shiro中使用的自定义缓存管理器
 */
public class RedisCacheManager implements CacheManager {
    /*
    每次授权判定都要来到这个方法，正在实现的是这个方法的返回值Cache
     */
    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        System.out.println("RedisCacheManager -> "+name);
        //RedisCache<Object, Object> redisCache = new RedisCache<>();
        return new RedisCache<>(name);  //先去查询redis缓存，如果return null直接去查询数据库
    }
}
