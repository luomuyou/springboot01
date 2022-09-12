package com.lmy.config;

import com.lmy.shiro.cache.RedisCacheManager;
import com.lmy.shiro.realm.CustomerRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 整合springboot与shiro之前的配置类,hh
 */
@Configuration
public class ShiroConfig {

    //0、创建shrioFilter,相当于一个拦截器，负责拦截所有请求的
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //【2】授权，权限管理
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        // 设置拦截器，设置公共资源
        Map<String, String> map = new HashMap<>();
        map.put("/user/login", "anon");     //不拦截认证请求
        map.put("/user/register", "anon");
        map.put("/login.html", "anon");
        map.put("/register.html", "anon");
        map.put("/user/logout", "anon");
        map.put("/user/JWTlogin", "anon");  //JWT登录
        //map.put("/user/JWTtest", "anon");  //
        /*主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
        authc 请求这个资源需要认证和授权,系统内中几乎所有的资源都要认证
        */
        map.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    //1、DefaultWebSecurityManager创建在web环境下的安全管理器；
    // 都是用参数的方式声明注入不需要再set了
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(Realm realm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //给安全管理器注入自定义的Reamlm
        defaultWebSecurityManager.setRealm(realm);
        return defaultWebSecurityManager;
    }
    //2、创建自定义的realm类
    @Bean
    public Realm getRealm(){
        CustomerRealm customerRealm = new CustomerRealm();
        //修改凭证校验匹配器
        //用户登录使用的密码明文与数据库中保存的密码密文不匹配，需要先把输入的密码明文转化为md5密文之后再匹配
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //设置realm使用的hash凭证
        hashedCredentialsMatcher.setHashAlgorithmName("md5");   //默认用md5来处理密文
        hashedCredentialsMatcher.setHashIterations(1024);    //再设置散列的次数

        customerRealm.setCredentialsMatcher(hashedCredentialsMatcher);

        /*
        开启缓存管理,不修改数据库得前提下不用每次都去数据库中获取数据进行对比，
        如果数据库中的数据修改了，再去查询数据库内容,
        new EhCacheManager()shiro自带的缓存管理器，后期可变化问自定义的redis缓存管理器
         */
        //customerRealm.setCacheManager(new EhCacheManager());
        customerRealm.setCacheManager(new RedisCacheManager());    //redis缓存管理器
        customerRealm.setCachingEnabled(true);      //开启全局缓存
        customerRealm.setAuthenticationCachingEnabled(true);    //认证缓存
        customerRealm.setAuthenticationCacheName("authenticationcache");    //起个名字
        customerRealm.setAuthorizationCachingEnabled(true);     //授权缓存
        customerRealm.setAuthorizationCacheName("authorizationcache");     //起个名字
        return customerRealm;
    }



}
