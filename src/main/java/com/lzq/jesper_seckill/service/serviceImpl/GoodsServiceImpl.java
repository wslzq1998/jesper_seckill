package com.lzq.jesper_seckill.service.serviceImpl;

import com.alibaba.druid.sql.PagerUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzq.jesper_seckill.dao.GoodsMapper;
import com.lzq.jesper_seckill.model.SeckillGoods;
import com.lzq.jesper_seckill.service.GoodsService;
import com.lzq.jesper_seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    GoodsMapper goodsMapper;

    /*
    * 查询商品列表
    * */
    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    /*
    * 分页查询
    * */
    @Override
    public PageInfo<GoodsVo> listPageGoodsVo(Integer pageNum, Integer pageSize) {
        //使用PageHelper实现分页
        PageHelper.startPage(pageNum,pageSize);
        List<GoodsVo> list = goodsMapper.listGoodsVo();
        PageInfo<GoodsVo> pageList = new PageInfo<>(list,pageSize);
        return pageList;
    }

    /*
    * 根据id查询指定商品
    * */
    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }
    /*
    * 减少库存，每次减一
    * */
    @Override
    public int reduceStock(GoodsVo goods) {
        int numAttempts = 0;
        int ret = 0;
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goods.getId());
        sg.setVersion(goods.getVersion());
        while(numAttempts<DEFAULT_MAX_RETRIES){
            numAttempts++;
            try{
                ret = goodsMapper.reduceStockByVersion(sg);
            }catch (Exception e){
                logger.info("减库存第{}次重试",numAttempts);
            }
        }

        return ret;
    }

    @Override
    public int getVersionByGoodsId(long goodsId) {
        return goodsMapper.getVersionByGoodsId(goodsId);
    }
}
