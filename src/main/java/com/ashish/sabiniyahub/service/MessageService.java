package com.ashish.sabiniyahub.service;

import com.ashish.sabiniyahub.model.Message;
import com.ashish.sabiniyahub.model.User;
import com.ashish.sabiniyahub.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void save(Message message){
        messageRepository.save(message);
    }

    public void deleteAll(List<Message> messages) {
        messageRepository.deleteAll(messages);
    }

    public List<Message> findAllBySenderIdAndRecipientId(String senderId, String recipientId){
        return messageRepository.findAllBySenderIdAndRecipientId(senderId, recipientId);
    }
}
