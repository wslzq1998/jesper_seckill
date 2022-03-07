package com.lzq.jesper_seckill.service.serviceImpl;

import com.lzq.jesper_seckill.dao.UserMapper;
import com.lzq.jesper_seckill.exception.GlobalException;
import com.lzq.jesper_seckill.model.User;
import com.lzq.jesper_seckill.result.CodeMsg;
import com.lzq.jesper_seckill.service.UserService;
import com.lzq.jesper_seckill.util.MD5Util;
import com.lzq.jesper_seckill.util.UUIDUtil;
import com.lzq.jesper_seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;

    public static final String COOKIE_NAME_TOKEN = "token";

    @Override
    public User getById(long id) {
        return userMapper.getById(id);
    }

    @Override
    public boolean updatePassword(String token, long id, String formPass) {
        return false;
    }

    public String login(HttpServletRequest request, LoginVo loginVo) {
        if(loginVo == null){
            throw  new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        User user = userMapper.getById(Long.valueOf(mobile));
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成唯一id作为token存入session中
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("LoginUser",user);
        //以秒为单位，即在没有活动10分钟后，session将失效
        httpSession.setMaxInactiveInterval(2*60*60);
        return mobile;
    }

}
