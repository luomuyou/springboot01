package com.lmy.shiro.cache;

import com.lmy.utils.ApplicationContextUtils;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.locks.ReadWriteLock;

//自定义redis作为mybatis缓存类，实现Cache接口，
public class RedisMybatisCache implements Cache {
    /*id为对应的mapper.xml文件中的 namespace="com.springboot004mybatis.mapper.UserMapper"*/
    private String id;

    public RedisMybatisCache(String id) {
        //System.out.println("RedisCache  id = "+id);
        this.id = id;
    }
    @Override
    public String getId() {
        //需要把这个id标识返回
        return id;
    }

    @Override
    public void putObject(Object o, Object o1) {
        System.out.println("put key"+o);
        System.out.println("put value"+o1);
        getRedisTemplate().opsForHash().put(id,o.toString(),o1);
    }

    @Override
    public Object getObject(Object o) {
        System.out.println("get key"+o);
        return getRedisTemplate().opsForHash().get(id,o.toString());
    }

    @Override
    public Object removeObject(Object o) {

        return getRedisTemplate().opsForHash().delete(id,o.toString());
    }

    @Override
    public void clear() {
        getRedisTemplate().opsForHash().delete(id);
    }

    @Override
    public int getSize() {
        return getRedisTemplate().opsForHash().size(id).intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    private RedisTemplate getRedisTemplate(){
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        redisTemplate.setKeySerializer(new StringRedisSerializer());    //序列化设置
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        System.out.println("redisTemplate "+redisTemplate);
        return redisTemplate;
    }
}
