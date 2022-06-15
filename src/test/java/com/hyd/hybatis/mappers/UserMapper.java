package com.hyd.hybatis.mappers;

import com.hyd.hybatis.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Update("create table users (user_id int primary key, user_name varchar(20))")
    void createUserTable();

    @Insert("insert into users values (#{userId}, #{userName})")
    int insertUser(@Param("userId") Long userId, @Param("userName") String userName);

    @Select("select * from users")
    List<User> selectAll();

    // Generate MappedStatement automatically for this method
    List<User> selectBySample(User user);

    // Ignore this method
    void anotherMethod();
}
