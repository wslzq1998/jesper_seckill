package com.lzq.jesper_seckill.auth;

import com.lzq.jesper_seckill.dao.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class UserDetailsServiceImpl implements UserDetailsService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UserMapper userMapper;

    /*
    * 这个方法要返回一个UserDetails对象,其中包括用户名，密码，授权信息等
    * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.lzq.jesper_seckill.model.User loginUser = userMapper.getById(Long.parseLong(username));
        if(loginUser==null) return null;
        logger.info("Security安全框架loadUserByUsername方法");
        //这是构造用户权限组的代码
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        list.add(authority);
        User user = new User(username, loginUser.getPassword(), list);
        return user;
    }
}
