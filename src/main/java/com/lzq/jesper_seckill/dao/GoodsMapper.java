package com.lzq.jesper_seckill.dao;

import com.lzq.jesper_seckill.model.SeckillGoods;
import com.lzq.jesper_seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsMapper {
    List<GoodsVo> listGoodsVo();
    GoodsVo getGoodsVoByGoodsId(long goodsId);
    int reduceStockByVersion(SeckillGoods seckillGoods);
    int getVersionByGoodsId(long goodsId);
}
