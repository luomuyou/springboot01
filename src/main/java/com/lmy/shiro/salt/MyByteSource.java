package com.lmy.shiro.salt;


import org.apache.shiro.util.ByteSource;

import java.io.Serializable;

/*
自定义盐类
    在CustomerRealm中ByteSource.Util.bytes(user.getSalt())作为参数注入SimpleAuthenticationInfo时
    ByteSource.Util.bytes(user.getSalt()) ：密码的MD5加密salt盐 处理
    序列化存入redis缓存时需要特殊处理一下这个salt,由于这个ByteSource.Util类没有实现序列化接口
    继承SimpleByteSource对象,实现Serializable接口
 */
public class MyByteSource extends org.apache.shiro.util.SimpleByteSource
        implements Serializable {

    public MyByteSource(byte[] bytes) {
        super(bytes);
    }
}
