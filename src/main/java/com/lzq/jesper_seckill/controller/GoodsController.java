package com.lzq.jesper_seckill.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.lzq.jesper_seckill.dao.UserMapper;
import com.lzq.jesper_seckill.model.Goods;
import com.lzq.jesper_seckill.model.Order;
import com.lzq.jesper_seckill.model.OrderInfo;
import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.result.CodeMsg;
import com.lzq.jesper_seckill.result.Result;
import com.lzq.jesper_seckill.service.GoodsService;
import com.lzq.jesper_seckill.service.OrderService;
import com.lzq.jesper_seckill.service.UserService;
import com.lzq.jesper_seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static com.lzq.jesper_seckill.rabbitMQ.rabbitMQConfig.*;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/list")
    @ResponseBody
    public List<GoodsVo> getList() {
        return goodsService.listGoodsVo();
    }

    /*
    * ???????????????????????????????????????
    * */
    @RequestMapping("/to_list")
    @ResponseBody
    public ModelAndView list(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        //?????????????????????????????????
        String  userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer pageNum = request.getParameter("pageNum")==null?1:Integer.parseInt(request.getParameter("pageNum"));
        PageInfo<GoodsVo> pageInfo = goodsService.listPageGoodsVo(pageNum,3);
        List<GoodsVo> goodsVoList = pageInfo.getList();
        model.addObject("user",userService.getById(Long.parseLong(userName)));
        model.addObject("goodsList", goodsVoList);
        //?????????
        model.addObject("pageNum", pageInfo.getPageNum());
        //?????????
        model.addObject("prePage", pageInfo.getPrePage());
        //?????????
        model.addObject("nextPage", pageInfo.getNextPage());
        //?????????
        model.addObject("maxPageNum", pageInfo.getPages());
        model.setViewName("goods_list");
        return model;
    }

    /*
    * ?????????????????????????????????
    * */
    @GetMapping("/detail/{goodsId}")
    @ResponseBody
    public ModelAndView detail(ModelAndView model, User user, @PathVariable("goodsId") String goodsId) {
        logger.info("?????????goodsId???"+goodsId);
        //??????id??????????????????
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(Long.valueOf(goodsId));

        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;

        if (now < startTime) {//??????????????????????????????
            seckillStatus = 0;
            remainSeconds = (int) ((startTime - now) / 1000);
        } else if (now > endTime) {//??????????????????
            seckillStatus = 2;
            remainSeconds = -1;
        } else {//???????????????
            seckillStatus = 1;
            remainSeconds = 0;
        }
        goods.setSeckillStatus(seckillStatus);
        goods.setRemainSeconds(remainSeconds);
        model.addObject("user", user);
        model.addObject("goods", goods);
        model.setViewName("goods_detail");
        return model;
    }

    /*
    * ????????????ID????????????
    * */
    @PostMapping(value = "/seckill")
    @ResponseBody
    public Result secKill(long goodsId){
        logger.info("??????????????????");
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getById(Long.parseLong(userName));
        logger.info("??????????????????"+user.toString());
        if(user == null){
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        logger.info("??????????????????"+user.toString());
        //??????????????????
        Order order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        GoodsVo goodsVo = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get("" + goodsId)),GoodsVo.class);
        Integer count = goodsVo.getStockCount();
        if(count<=0){
            return Result.error(CodeMsg.SECKILL_OVER);
        }else{
            goodsVo.setGoodsStock(goodsVo.getGoodsStock()-1);
            redisTemplate.opsForValue().set(""+goodsId,goodsVo);
            //JSON.toJSONString(user.getId()+"#"+goodsId)
            rabbitTemplate.convertAndSend(BUSINESS_EXCHANGE_NAME, BUSINESS_QUEUEA_ROUTING_KEY, JSON.toJSONString(user.getId()+"#"+goodsId), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    //????????????
                    messageProperties.setContentEncoding("utf-8");
                    //??????????????????60*1000??????
//                    messageProperties.setExpiration("60000");
                    //??????????????????ID
                    messageProperties.setCorrelationId(UUID.randomUUID().toString());
                    return message;
                }
            });
            return Result.success(0);
        }
    }
}
