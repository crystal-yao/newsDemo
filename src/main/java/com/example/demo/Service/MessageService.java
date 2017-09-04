package com.example.demo.Service;

import com.example.demo.dao.MessageDAO;
import com.example.demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Crys at 2017/9/3
 */
@Service
public class MessageService {

    @Autowired
    private MessageDAO messageDAO;

    public int addMessage(Message msg) {
        return messageDAO.addMessage(msg);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public int getConversationUnReadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }

    public void updateHasRead(int toId,String conversationId) {
        messageDAO.updateHasRead(toId,conversationId);
    }
}
