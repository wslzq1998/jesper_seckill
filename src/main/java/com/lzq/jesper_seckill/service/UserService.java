package com.lzq.jesper_seckill.service;

import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.vo.LoginVo;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    public User getById(long id);
    public boolean updatePassword(String token, long id, String formPass);
    public String login(HttpServletRequest request, LoginVo loginVo);
}
