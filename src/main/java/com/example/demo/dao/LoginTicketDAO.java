package com.example.demo.dao;

import com.example.demo.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by Crys at 2017/8/19
 * 登录时给用户一个ticket，并把信息保存在数据库中；
 * 这个dao层具备插入ticket和通过ticket字符串获取ticket的功能
 */
@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = "user_id, ticket, expired, status";
    String SELECT_FIELDS = "id," + INSERT_FIELDS;
    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId}, #{ticket },#{expired},#{status})"})
    int addLoginTicket(LoginTicket ticket);

    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket")String ticket, @Param("status") int status);

    @Delete({"delete from ", TABLE_NAME, " where id = #{id}"})
    void deleteById(int id);
}
