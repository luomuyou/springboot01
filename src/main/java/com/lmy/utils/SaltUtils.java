package com.lmy.utils;

import java.util.Random;

public class SaltUtils {
    public static void main(String[] args) {
        System.out.println(getSalt(3));
    }
    //生成随机salt方法工具
    public static String getSalt(int n){
        //随机字符串转化为数组
        String ss = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()<>?:{}[];',./";
        char[] chars = ss.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {   //次数
            //随产生某个范围内的整数
            char aChar = chars[new Random().nextInt(chars.length)];
            stringBuilder.append(aChar);
        }
        System.out.println("salt:"+stringBuilder.toString());
        return stringBuilder.toString();
    }
}
