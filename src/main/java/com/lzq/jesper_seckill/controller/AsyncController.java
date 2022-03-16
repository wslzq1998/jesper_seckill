package com.lzq.jesper_seckill.controller;

import com.lzq.jesper_seckill.service.serviceImpl.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@EnableAsync
public class AsyncController {
    @Autowired
    private AsyncService asyncService;

    @GetMapping("/query")
    @ResponseBody
    public String asyncTest() {
        Future<String> future = asyncService.getDataResult();
        asyncService.getDataResultTwo();
        String result = null;
        try{
            result =future.get();
        }catch (ExecutionException | InterruptedException e){
            e.printStackTrace();
        }

        return result;
    }
}
