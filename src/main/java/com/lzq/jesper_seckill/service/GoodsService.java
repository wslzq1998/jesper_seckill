package com.lzq.jesper_seckill.service;

import com.github.pagehelper.PageInfo;
import com.lzq.jesper_seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface GoodsService {
    List<GoodsVo> listGoodsVo();
    PageInfo<GoodsVo> listPageGoodsVo(Integer pageNum, Integer pageSize);
    GoodsVo getGoodsVoByGoodsId(long goodsId);
    int reduceStock(GoodsVo goods);
    int getVersionByGoodsId(long goodsId);
}
