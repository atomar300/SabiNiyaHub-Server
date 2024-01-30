package com.ashish.sabiniyahub.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String senderId;

    private String recipientId;

    private MessageType messageType;

    private String message;

    private String imageUrl;

    private LocalDateTime timeStamp = LocalDateTime.now();

//    public Message(String senderId, String recipientId,  MessageType messageType, String message, String imageUrl){
//        this.senderId = senderId;
//        this.recipientId = recipientId;
//        this.messageType = messageType;
//        this.message = message;
//        this.imageUrl = imageUrl;
//    }

    public Message(String senderId, String recipientId,  MessageType messageType, String message){
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.messageType = messageType;
        this.message = message;
    }

}
