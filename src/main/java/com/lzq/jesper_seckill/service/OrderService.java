package com.lzq.jesper_seckill.service;

import com.lzq.jesper_seckill.model.Order;
import com.lzq.jesper_seckill.model.OrderInfo;
import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.vo.GoodsVo;
import com.lzq.jesper_seckill.vo.OrderDetailVo;

import java.util.List;

public interface OrderService {
    Order getOrderByUserIdGoodsId(long userId, long goodsId);
    OrderInfo getOrderById(long orderId);
    OrderInfo createOrder(User user, GoodsVo goods);
    List<OrderDetailVo> getOrdersList(Long userId);
    void deleteByOrderId(long orderId,long userId);
}
