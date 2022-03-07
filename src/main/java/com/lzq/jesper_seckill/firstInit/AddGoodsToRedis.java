package com.lzq.jesper_seckill.firstInit;

import com.lzq.jesper_seckill.service.GoodsService;
import com.lzq.jesper_seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class AddGoodsToRedis implements CommandLineRunner {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private GoodsService goodsService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void run(String... args) throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        for(GoodsVo goodsVo:goodsVoList){
            logger.info("Redis初始化加载秒杀商品ID和数量："+goodsVo.getId().toString()+" "+goodsVo.getStockCount().toString());
            redisTemplate.opsForValue().set(goodsVo.getId().toString(),goodsVo);
        }
    }
}
