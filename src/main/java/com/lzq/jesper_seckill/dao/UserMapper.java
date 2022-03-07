package com.lzq.jesper_seckill.dao;

import com.lzq.jesper_seckill.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getById(long id);

    void update(User toBeUpdate);
}
