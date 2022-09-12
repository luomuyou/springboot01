package com.lmy.utils;

import com.lmy.shiro.salt.MyByteSource;
import org.apache.shiro.util.ByteSource;

public class ByteSourceUtils{
    public static ByteSource bytes(byte[] bytes){
        return new MyByteSource(bytes);
    }
    public static ByteSource bytes(String arg0){
        return new MyByteSource(arg0.getBytes());
    }
}