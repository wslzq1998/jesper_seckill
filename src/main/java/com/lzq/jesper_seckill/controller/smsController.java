package com.lzq.jesper_seckill.controller;

import com.lzq.jesper_seckill.util.UUIDUtil;
import com.lzq.jesper_seckill.util.smsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class smsController {

    @RequestMapping("smsSend/{mobile}")
    @ResponseBody
    public String smsTest(@PathVariable("mobile") String mobile){
        String smsCode = UUIDUtil.generateVerificationCode(6);
        String res="";
        try {
            res = smsUtil.sendSms(smsUtil.APIKEY,smsUtil.SIGN+smsUtil.TEMPLATE+smsCode,mobile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
