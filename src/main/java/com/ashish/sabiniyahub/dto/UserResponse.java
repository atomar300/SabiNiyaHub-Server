package com.ashish.sabiniyahub.dto;

import com.ashish.sabiniyahub.model.User;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {

    // default value is true
    private boolean success = true;

    private String message;

    private User user;

    private List<User> users;

    private List<String> friendRequests;

    private List<String> friends;

    private String token;

}
