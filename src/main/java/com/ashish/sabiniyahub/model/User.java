package com.ashish.sabiniyahub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @JsonIgnore
    @NotBlank
    private String password;

    private String image;

    private List<String> friendRequests = new ArrayList<>();

    private List<String> friends = new ArrayList<>();

    private List<String> sentFriendRequests = new ArrayList<>();

    public User(String name, String email, String password, String image){
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
    }
}
