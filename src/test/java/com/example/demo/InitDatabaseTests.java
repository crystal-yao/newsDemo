package com.example.demo;

import com.example.demo.DemoApplication;
import com.example.demo.Service.QiniuService;
import com.example.demo.dao.CommentDAO;
import com.example.demo.dao.NewsDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.dao.LoginTicketDAO;

import com.example.demo.model.*;
import com.example.demo.util.MD5util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
//@WebAppConfiguration  这行会修改默认的启动路径需要注释掉
@Sql({"/init-schema.sql"})
public class InitDatabaseTests {

    @Autowired
    UserDAO userDAO;
    @Autowired
    NewsDAO newsDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    CommentDAO commentDAO;

    @Test
    public void contextLoads() {
        Random random = new Random();
        for(int i = 0; i < 10; i ++) {
            User user = new User();
            user.setHeadUrl(String.format("http://ovamc2vwk.bkt.clouddn.com/image/head/touxiang%d.png", random.nextInt(5)));
            user.setName(String.format("wang%d",i));
            user.setSalt(UUID.randomUUID().toString().substring(0,5));
            user.setPassword(MD5util.MD5("password"+user.getSalt()));
            userDAO.addUser(user);

            News news = new News();
            news.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*i*3600*5);
            news.setCreatedDate(date);
            news.setImage(String.format("http://ovamc2vwk.bkt.clouddn.com/image/news/skin%d.png", random.nextInt(10)));
            news.setLikeCount(random.nextInt(20));
            news.setUserId(user.getId());
            news.setTitle(String.format("Title{%d}",i)) ;
            news.setLink("http://127.0.0.1:8080/index");
            newsDAO.addNews(news);
            //修改密码
            //user.setPassword(MD5util.MD5("password"+user.getSalt()));
           // userDAO.updatePassword(user);
           // System.out.println(news.getId());

            //给新闻添加评论
            for(int j=0; j<3; j++) {
                Comment comment = new Comment();
                comment.setUserId(random.nextInt(9)+1);
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                comment.setCreatedDate(new Date());
                comment.setStatus(0);
                comment.setContent("Comment from " + String.valueOf(comment.getUserId()));
                commentDAO.addComment(comment);
            }

            //每人都登录过的记录，但ticket都过时了
            LoginTicket ticket = new LoginTicket();
            ticket.setUserId(i+1);
            ticket.setExpired(date);
            ticket.setStatus(0);
            ticket.setTicket(String.format("ticket%d",i+1));
            loginTicketDAO.addLoginTicket(ticket);

            loginTicketDAO.updateStatus(ticket.getTicket(),2);
        }

        //查找和删除数据测试
        /*Assert.assertEquals(MD5util.MD5("password"+userDAO.selectById(1).getSalt()), userDAO.selectById(1).getPassword());
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));

        Assert.assertEquals(1, loginTicketDAO.selectByTicket("ticket1").getUserId());
        Assert.assertEquals(2, loginTicketDAO.selectByTicket("ticket1").getStatus());*/
        Assert.assertNotNull(commentDAO.selectByEntity(1, EntityType.ENTITY_NEWS).get(0));
    }
}
