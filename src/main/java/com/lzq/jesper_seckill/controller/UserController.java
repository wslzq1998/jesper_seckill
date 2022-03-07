package com.lzq.jesper_seckill.controller;

import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    @ResponseBody
    public User selectById(@PathVariable("id") Long id){
        return userService.getById(id);
    }

    @GetMapping("/session")
    @ResponseBody
    public String getSession(){
        String  userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userName;
    }
}
