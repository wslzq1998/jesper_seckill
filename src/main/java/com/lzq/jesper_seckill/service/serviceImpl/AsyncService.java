package com.lzq.jesper_seckill.service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AsyncService {
    private Logger logger = LoggerFactory.getLogger(AsyncService.class);

    /**
     * @Async注解表示异步，后面的参数对应于线程池配置类ExecutorConfig中的方法名asyncServiceExecutor()，
     * 如果不写后面的参数，直接使用@Async注解，则是使用默认的线程池
     * Future<String>为异步返回的结果。可以通过get()方法获取结果。
     *
     */
    @Async("asyncServiceExecutor")
    public Future<String> getDataResult(){
        logger.info("asyncResultTest");
        String result="success";
        return new AsyncResult<String>(result);
    }
    @Async("asyncServiceExecutor")
    public Future<String> getDataResultTwo(){
        logger.info("asyncResultTestTwo");
        String result="successTwo";
        return new AsyncResult<String>(result);
    }
}
