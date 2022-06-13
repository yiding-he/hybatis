package com.hyd.hybatis.mappers;

import com.hyd.hybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from users")
    List<User> selectAll();

    List<User> selectBySample(User user);
}
