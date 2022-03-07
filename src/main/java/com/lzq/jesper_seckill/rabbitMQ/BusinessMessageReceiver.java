package com.lzq.jesper_seckill.rabbitMQ;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzq.jesper_seckill.model.Order;
import com.lzq.jesper_seckill.model.OrderInfo;
import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.service.OrderService;
import com.lzq.jesper_seckill.vo.GoodsVo;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static com.lzq.jesper_seckill.rabbitMQ.rabbitMQConfig.*;

@Component
public class BusinessMessageReceiver {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = BUSINESS_QUEUEA_NAME)
    public void receiveA(Message message, Channel channel) throws IOException{
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//        channel.basicAck(该消息的index,是否批量.true:将一次性ack所有小于deliveryTag的消息);
//        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
//        channel.basicNack(该消息的index,是否批量.true:将一次性拒绝所有小于deliveryTag的消息,被拒绝的是否重新入队列);
        GoodsVo goodsVo = null;
        try{
//            JSONObject json = JSON.parseObject(new String(message.getBody()));
//            String str = JSON.parseObject(json.toJSONString(),String.class);
            String str = JSON.parseObject(new String(message.getBody()),String.class);
            System.out.println("RabbitMQ接收的数据为"+str);
            User user = new User();
            user.setId(Long.valueOf(str.split("#")[0]));
            goodsVo = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get(str.split("#")[1])),GoodsVo.class);
            OrderInfo orderInfo = orderService.createOrder(user, goodsVo);
            //订单生成，发送延迟队列
            rabbitTemplate.convertAndSend(BUSINESS_EXCHANGE_NAME, BUSINESS_QUEUEB_ROUTING_KEY, JSON.toJSONString(user.getId()+"#"+orderInfo.getId()), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    //设置编码
                    messageProperties.setContentEncoding("utf-8");
                    //设置过期时间60*1000毫秒
                    messageProperties.setExpiration("60000");
                    //设置全局唯一ID
                    messageProperties.setCorrelationId(UUID.randomUUID().toString());
                    return message;
                }
            });
            logger.info("发送至死信队列");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e) {
            e.printStackTrace();
            rabbitTemplate.convertAndSend(BUSINESS_EXCHANGE_NAME, BUSINESS_QUEUEA_ROUTING_KEY, message.getBody(), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    //设置编码
                    messageProperties.setContentEncoding("utf-8");
                    //设置过期时间60*1000毫秒
//                    messageProperties.setExpiration("60000");
                    //设置全局唯一ID
                    messageProperties.setCorrelationId(UUID.randomUUID().toString());
                    return message;
                }
            });
        }
    }
//    @RabbitListener(queues = BUSINESS_QUEUEB_NAME)
    public void receiveB(Message message, Channel channel) throws IOException{

    }
    @RabbitListener(queues = DEAD_LETTER_QUEUE_NAME)
    public void receiveDeadLetterB(Message message,Channel channel) throws IOException {
        logger.info("死信队列接收到");
        String str = JSON.parseObject(new String(message.getBody()),String.class);
        logger.info("RabbitMQ接收的数据为"+str);
        Long userId = Long.valueOf(str.split("#")[0]);
        Long goodId = Long.valueOf(str.split("#")[1]);
        Order order = orderService.getOrderByUserIdGoodsId(userId,goodId);
        if(order==null){
            logger.info("延时队列：该订单已删除");
        }else{
            Long orderId = order.getOrderId();
            OrderInfo orderInfo = orderService.getOrderById(orderId);
            if(!orderInfo.getStatus().equals("0")){
                logger.info("延时队列：该订单已完成支付");
            }else{
                orderService.deleteByOrderId(goodId,userId);
                logger.info("延时队列：订单超时，删除订单,OrderId为"+Long.valueOf(str.split("#")[1]));
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
