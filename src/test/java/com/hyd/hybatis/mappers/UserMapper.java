package com.hyd.hybatis.mappers;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.entity.User;
import com.hyd.hybatis.entity.UserCteQuery;
import com.hyd.hybatis.entity.UserQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Update("create table users (user_id int primary key, user_name varchar(20))")
    void createUserTable();

    @Insert("insert into users values (#{userId}, #{userName})")
    int insertUser(@Param("userId") Long userId, @Param("userName") String userName);

    @Select("select * from users")
    List<User> selectAll();

    //////////////////////////// Generate MappedStatement automatically for these methods

    @HbQuery(table = "users")
    List<User> selectByQuery(UserQuery userQuery);

    @HbQuery(table = "select * from users where user_name is not null")
    List<User> selectByQueryCte(UserCteQuery userQuery);

    @HbQuery(table = "users")
    List<User> selectByConditions(Conditions conditions);

    @HbQuery(table = "users")
    List<Map<String, Object>> selectMapByQuery(UserQuery userQuery);

    @HbInsert(table = "users")
    int insertUserObject(User user);

    @HbUpdate(table = "users")
    void updateUser(UserQuery query, User update);

    //////////////////////////// Ignore these methods
    void anotherMethod();
}
