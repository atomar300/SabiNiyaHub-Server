package com.ashish.sabiniyahub.dto;

import com.ashish.sabiniyahub.model.Message;
import lombok.Data;

import java.util.List;

@Data
public class MessageResponse {

    private boolean success = true;

    private List<Message> messages;

    private String message;
}
