package com.example.demo.Service;

import com.example.demo.dao.LoginTicketDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.LoginTicket;
import com.example.demo.model.User;
import com.example.demo.util.MD5util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String , Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<String,Object>();
        if(StringUtils.isBlank(username)) {
            map.put("msgname","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("msgpwd","密码不能为空！");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user != null) {
            map.put("msgname","用户名已被注册！");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://ovamc2vwk.bkt.clouddn.com/image/head/touxiang%d.png", new Random().nextInt(5)));
        user.setPassword(MD5util.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //注册成功后自动进入登录状态，下发ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public Map<String , Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String,Object>();
        if(StringUtils.isBlank(username)) {
            map.put("msgname","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("msgpwd","密码不能为空！");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user == null) {
            map.put("msgname","用户名不存在！");
            return map;
        }

        //验证密码
        if(!MD5util.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd","密码不正确");
            return map;
        }

        //登录成功，下发ticket.
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket,1);
    }

    //完成下发ticket的具体功能
    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        ticket.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime()+ 1000*3600*12);
        ticket.setExpired(date);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-","") );
        loginTicketDAO.addLoginTicket(ticket);
        return ticket.getTicket();
    }
}
