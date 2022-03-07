package com.lzq.jesper_seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class errorController {

    @RequestMapping("/error/{msg}")
    public String error(Model model, @PathVariable("msg")String msg){
        model.addAttribute("errmsg",msg);
        return "seckill_fail";
    }
}
