package com.lzq.jesper_seckill.util;

import java.util.UUID;

/**
 * Created by jiangyunxiong on 2018/5/22.
 * <p>
 * 唯一id生成类
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static String generateVerificationCode(int length){
        StringBuilder sb = new StringBuilder();
        for(int i =0 ;i < length ;i ++){
            sb.append((int)(Math.random() * 9)  + 1);
        }
        return sb.toString();
    }

}
