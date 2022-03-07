package com.lzq.jesper_seckill.service.serviceImpl;

import com.lzq.jesper_seckill.dao.OrderMapper;
import com.lzq.jesper_seckill.model.Order;
import com.lzq.jesper_seckill.model.OrderInfo;
import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.service.GoodsService;
import com.lzq.jesper_seckill.service.OrderService;
import com.lzq.jesper_seckill.vo.GoodsVo;
import com.lzq.jesper_seckill.vo.OrderDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    GoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Order getOrderByUserIdGoodsId(long userId, long goodsId) {
        return orderMapper.getOrderByUserIdGoodsId(userId,goodsId);
    }

    @Override
    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }

    /**
     * 因为要同时分别在订单详情表和秒杀订单表都新增一条数据，所以要保证两个操作是一个事物
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderMapper.insert(orderInfo);

        Order order = new Order();
        order.setGoodsId(goods.getId());
        order.setOrderId(orderInfo.getId());
        order.setUserId(user.getId());
        orderMapper.insertSeckillOrder(order);

        int updateCount = goodsService.reduceStock(goods);
        int version = goodsService.getVersionByGoodsId(goods.getId());
        goods.setVersion(version);
        redisTemplate.opsForValue().set(""+goods.getId(),goods);
        logger.info("减库存成功，当前ID为{}的商品的版本号为{}",goods.getId(),version);
        return orderInfo;
    }

    @Override
    public List<OrderDetailVo> getOrdersList(Long userId) {
        return orderMapper.getOrdersList(userId);
    }

    @Transactional
    public void deleteByOrderId(long orderId,long userId) {
        orderMapper.deleteOrderById(orderId,userId);
        orderMapper.deleteOrderInfoById(orderId,userId);
    }
}
