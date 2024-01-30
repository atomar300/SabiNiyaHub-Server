package com.ashish.sabiniyahub.dto;

import lombok.Data;

@Data
public class FriendRequest {

    private String currentUserId;
    private String selectedUserId;
    private String senderId;
    private String recipientId;
}
