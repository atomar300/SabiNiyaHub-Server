package com.ashish.sabiniyahub.dto;

import com.ashish.sabiniyahub.model.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageDto {

    private String senderId;

    private String recipientId;

    private String messageType;

    private String message;

    private String base64Image;

    private List<Message> messages = new ArrayList<>();

}
