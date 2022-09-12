package com.lmy.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {

    private static final String SING = "!@#$%";     //指定加密秘钥，一般可以配合用户名为密钥使用

    //token生成方法，（controller登录调用）
    public static String getToken(Map<String,String> map){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,7);     //过期时间选Calendar.DATE天数

        JWTCreator.Builder builder = JWT.create();
        map.forEach((k,v) -> {     //遍历填充进数据  用户的基本信息(ID和UserName)
            builder.withClaim(k,v);
        });
        String token = builder
                .withExpiresAt(instance.getTime())  //过期时间为7天后
                .sign(Algorithm.HMAC256(SING));  //秘钥签名,HMAC256算法加密
        return token;
    }
    //验证方法,自定义拦截器调用认证
    public static void vertify(String token){
        System.out.println("token验证==============="+token);
        //就是把新传进来的token用HMAC256解密
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);//如果该语句没有报异常验证通过
    }
    //验证 + 获取token信息（controller二次调用）
    public static DecodedJWT getTokenInfo(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
        return  verify;
    }
}
