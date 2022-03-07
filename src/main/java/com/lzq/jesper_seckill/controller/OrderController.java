package com.lzq.jesper_seckill.controller;

import com.lzq.jesper_seckill.model.Order;
import com.lzq.jesper_seckill.model.OrderInfo;
import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.result.CodeMsg;
import com.lzq.jesper_seckill.result.Result;
import com.lzq.jesper_seckill.service.GoodsService;
import com.lzq.jesper_seckill.service.OrderService;
import com.lzq.jesper_seckill.service.UserService;
import com.lzq.jesper_seckill.vo.GoodsVo;
import com.lzq.jesper_seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;

    /*
    * 获取订单列表并返回视图
    * */
    @RequestMapping("/ordersList")
    @ResponseBody
    public ModelAndView ordersList(HttpServletRequest request,ModelAndView modelAndView){
        //获取当前登录用户手机号
        String  userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getById(Long.parseLong(userName));
        List<OrderDetailVo> ordersList = orderService.getOrdersList(user.getId());
        modelAndView.addObject("user",user);
        modelAndView.addObject("ordersList",ordersList);
        modelAndView.setViewName("orders_list");
        return modelAndView;
    }

    /*
    * 根据订单ID获取订单的详细信息
    * */
    @RequestMapping("/orderDetail/{orderId}")
    @ResponseBody
    public ModelAndView go(ModelAndView modelAndView,@PathVariable("orderId") long orderId){
        modelAndView.addObject("orderId",orderId);
        modelAndView.setViewName("order_detail");
        return modelAndView;
    }

    /*
    *  根据订单ID获取订单的详细信息
    * */
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(@RequestParam("orderId") long orderId) {
        //获取当前登录用户手机号
        String  userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getById(Long.parseLong(userName));
        if(user == null){
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
    /*
    * 根据订单ID删除订单
    * */
    @RequestMapping("/delete/{id}")
    @ResponseBody
    public ModelAndView deleteByUidOid(ModelAndView modelAndView,@PathVariable("id") long orderId) {
        //获取当前登录用户手机号
        String  userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getById(Long.parseLong(userName));
        orderService.deleteByOrderId(orderId,user.getId());
        modelAndView.setViewName("redirect:/order/ordersList");
        return modelAndView;
    }
}
