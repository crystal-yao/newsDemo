package com.example.demo.Controller;

import com.example.demo.Service.MessageService;
import com.example.demo.Service.UserService;
import com.example.demo.dao.CommentDAO;
import com.example.demo.dao.MessageDAO;
import com.example.demo.model.*;
import com.example.demo.util.MD5util;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Crys at 2017/9/3
 */
@Controller
public class MessageController {

    private static final Logger logger =  LoggerFactory.getLogger(loginController.class.toString());

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = {"/msg/list"}, method = { RequestMethod.GET})
    public String conversationList(Model model) {
        try{
            int LocalUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(LocalUserId,0,10);
            for(Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = (LocalUserId == msg.getFromId()) ? msg.getToId() : msg.getFromId();
                User user= userService.getUser(targetId);
                vo.set("target", user);
                vo.set("unread", messageService.getConversationUnReadCount(LocalUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败！" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = { RequestMethod.GET})
    public String conversationDetail(Model model, @Param("conversationId") String conversationId) {
        try{
            List<Message> conversationList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user= userService.getUser(msg.getFromId());
                if(user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
            int LocalUserId=hostHolder.getUser().getId();
            messageService.updateHasRead(LocalUserId,conversationId);
        } catch (Exception e) {
            logger.error("获取详细消息失败！" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"}, method = { RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try{
            Message message = new Message();
            message.setContent(content);
            message.setFromId(fromId);
            message.setToId(toId);
            message.setHasRead(0);
            message.setCreatedDate(new Date());
            message.setConversationId( fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId) );
            messageService.addMessage(message);
            return MD5util.getJSONString( message.getId() );
        }catch( Exception e ) {
            logger.error("增加站内信失败！" + e.getMessage());
            return MD5util.getJSONString(1, "插入站内信失败");
        }
    }
}
