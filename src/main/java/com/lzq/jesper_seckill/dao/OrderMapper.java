package com.lzq.jesper_seckill.dao;

import com.lzq.jesper_seckill.model.Order;
import com.lzq.jesper_seckill.model.OrderInfo;
import com.lzq.jesper_seckill.vo.OrderDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    Order getOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);
    long insert(OrderInfo orderInfo);
    int insertSeckillOrder(Order order);
    OrderInfo getOrderById(@Param("orderId")long orderId);
    List<OrderDetailVo> getOrdersList(Long userId);
    void deleteOrderById(long orderId,long userId);
    void deleteOrderInfoById(long orderId,long userId);
}
