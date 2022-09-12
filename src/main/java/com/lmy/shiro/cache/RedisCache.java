package com.lmy.shiro.cache;


import com.lmy.utils.ApplicationContextUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/*
redis缓存真正的实现
 */
public class RedisCache<K,V> implements Cache<K,V> {

    private RedisTemplate<Object,Object> redisTemplate;
    //private RedisTemplate<String,Object> redisTemplate;

    private String name;

    public RedisCache(String name) {
        this.name = name;
    }

    public RedisCache() {
    }

    //从缓存中获取数据
    @Override
    public V get(K k) throws CacheException {
        System.out.println("get key : " + k);
        redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        //V res = (V) redisTemplate.opsForValue().get(k.toString());
        //<String1,<String2,Object>> 的形式存储到redis缓存中
        V res = (V) redisTemplate.opsForHash().get(name,k.toString());
        return res;
    }

    //把数据库中的查询结果放入缓存中
    @Override
    public V put(K k, V v) throws CacheException {
        //缓存中加入键值对模拟
        System.out.println("put key : "+ k);
        System.out.println("put value : "+ v);
        redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        //redisTemplate.opsForValue().set(k.toString(),v);
        redisTemplate.opsForHash().put(name,k.toString(),v);
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        return (V)redisTemplate.opsForHash().delete(this.name,k.toString());
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        redisTemplate.opsForHash().delete(this.name);
    }

    @Override
    public int size() {
        redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        Long size = redisTemplate.opsForHash().size(this.name);
        return size.intValue();
    }

    @Override
    public Set<K> keys() {
        redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        return (Set<K>) redisTemplate.opsForHash().keys(this.name);
    }

    @Override
    public Collection<V> values() {
        redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        return (Collection<V>) redisTemplate.opsForHash().values(this.name);
    }
}
