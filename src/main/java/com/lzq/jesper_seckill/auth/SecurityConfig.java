package com.lzq.jesper_seckill.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public PasswordEncoder passwordEncoder(){
        // 使用BCrypt加密密码
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    /*
    * 配置用户权限组和接口路径的关系和一些其他配置
    * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests() // 对请求进行验证
                .antMatchers("/").permitAll()
                .antMatchers("/goods/**").hasRole("USER")// 必须有USER权限
                .antMatchers("/order/**").hasRole("USER")
                .and()
                .formLogin()
                .usernameParameter("mobile")
                .passwordParameter("password")
                .loginPage("/login/to_login")
                .successForwardUrl("/goods/to_list")
                .and()
                .csrf().disable()
        ;
    }
}
