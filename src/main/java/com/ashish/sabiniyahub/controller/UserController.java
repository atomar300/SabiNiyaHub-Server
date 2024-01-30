package com.ashish.sabiniyahub.controller;

import com.ashish.sabiniyahub.dto.*;
import com.ashish.sabiniyahub.exception.UserNotFoundException;
import com.ashish.sabiniyahub.model.Message;
import com.ashish.sabiniyahub.model.MessageType;
import com.ashish.sabiniyahub.model.User;
import com.ashish.sabiniyahub.security.JwtUtils;
import com.ashish.sabiniyahub.service.MessageService;
import com.ashish.sabiniyahub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private PasswordEncoder encoder;
    private JwtUtils jwtUtils;

    private UserService userService;

    private MessageService messageService;

    @Autowired
    UserController(PasswordEncoder encoder, JwtUtils jwtUtils, UserService userService, MessageService messageService) {
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.messageService = messageService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignupRequest userSignupRequest) throws Exception {
        if (userService.existsByEmail(userSignupRequest.getEmail())) {
            throw new Exception("Email is already taken!");
        }

        // Receiving a base64 string of image from the front-end using FormData with json data.
        String base64 = userSignupRequest.getImage();
        String image = userService.processImage(base64, "images");

        // Create new user's account
        User user = new User(userSignupRequest.getName(),
                userSignupRequest.getEmail(),
                encoder.encode(userSignupRequest.getPassword()),
                image
        );

        userService.save(user);

        UserResponse response = new UserResponse();
        response.setMessage("User Registered Successfully!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) throws BadCredentialsException {
        if (!userService.existsByEmail(loginRequest.getEmail())) {
            throw new UserNotFoundException("No User found with the given Email: " + loginRequest.getEmail());
        }

        String jwt = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        User user = userService.findById(jwtUtils.getIdFromJwtToken(jwt));

        UserResponse response = new UserResponse();
        response.setUser(user);
        response.setToken(jwt);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/me")
    public ResponseEntity<?> loadUser(HttpServletRequest request)  {;

        User user = userService.getUserFromJwt(request);

        UserResponse response = new UserResponse();
        response.setUser(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<?> allUsersExceptLoggedIn(@PathVariable("userId") String userId) {

        List<User> users = userService.findAll();

        users.remove(userService.findById(userId));

        UserResponse response = new UserResponse();
        response.setUsers(users);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @GetMapping("/users")
    public ResponseEntity<?> searchUsers(@RequestParam(value = "search", required = false) String search, HttpServletRequest request) {

        User loggedInUser = userService.getUserFromJwt(request);
        List<User> users = userService.searchUsers(search, loggedInUser.getId());

        UserResponse response = new UserResponse();
        response.setUsers(users);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/friend-request")
    public ResponseEntity<?> sendRequest(@RequestBody FriendRequest friendRequest) {

        User selectedUser = userService.findById(friendRequest.getSelectedUserId());
        selectedUser.getFriendRequests().add(friendRequest.getCurrentUserId());
        userService.save(selectedUser);

        User currentUser = userService.findById(friendRequest.getCurrentUserId());
        currentUser.getSentFriendRequests().add(friendRequest.getSelectedUserId());
        userService.save(currentUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/friend-request/{userId}")
    public ResponseEntity<?> retrieveAllFriendRequests(@PathVariable("userId") String userId) {
        User user = userService.findById(userId);
        UserResponse response = new UserResponse();
        response.setFriendRequests(user.getFriendRequests());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/friend-request/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendRequest friendRequest) {
        User sender = userService.findById(friendRequest.getSenderId());
        User recipient = userService.findById(friendRequest.getRecipientId());

        sender.getFriends().add(recipient.getId());
        recipient.getFriends().add(sender.getId());

        recipient.getFriendRequests().remove(sender.getId());
        sender.getSentFriendRequests().remove(recipient.getId());

        userService.save(sender);
        userService.save(recipient);

        UserResponse response = new UserResponse();
        response.setMessage("Friend Request Accepted!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/accepted-friends/{userId}")
    public ResponseEntity<?> getAllFriends(@PathVariable("userId") String userId) {
        User user = userService.findById(userId);

        UserResponse response = new UserResponse();
        response.setFriends(user.getFriends());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {

        if (messageDto.getMessageType().equals("image")) {
            String base64 = messageDto.getBase64Image();
            String imageUrl = userService.processImage(base64, "photos");

            Message message = new Message(
                    messageDto.getSenderId(),
                    messageDto.getRecipientId(),
                    MessageType.IMAGE,
                    messageDto.getMessage()
            );
            message.setImageUrl(imageUrl);
            messageService.save(message);
        } else {
            Message message = new Message(
                    messageDto.getSenderId(),
                    messageDto.getRecipientId(),
                    MessageType.TEXT,
                    messageDto.getMessage()
            );
            messageService.save(message);
        }

        UserResponse response = new UserResponse();
        response.setMessage("Message sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable("userId") String userId) {
        User recipient = userService.findById(userId);
        UserResponse response = new UserResponse();
        response.setUser(recipient);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> fetchMessages(@PathVariable("senderId") String senderId, @PathVariable("recipientId") String recipientId) {

        List<Message> messages = messageService.findAllBySenderIdAndRecipientId(senderId, recipientId);

        MessageResponse response = new MessageResponse();
        response.setMessages(messages);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/deleteMessages")
    public ResponseEntity<?> deleteMessages(@RequestBody MessageDto messageDto) {
        messageService.deleteAll(messageDto.getMessages());
        MessageResponse response = new MessageResponse();
        response.setMessage("Message delete successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/friend-requests/sent/{userId}")
    public ResponseEntity<?> retrieveAllSendFriendRequests(@PathVariable("userId") String userId) {
        User user = userService.findById(userId);
        List<String> sentFriendRequests = user.getSentFriendRequests();
        return new ResponseEntity<>(sentFriendRequests, HttpStatus.OK);
    }


    @GetMapping("/friends/{userId}")
    public ResponseEntity<?> retrieveAllFriendsId(@PathVariable("userId") String userId) {
        User user = userService.findById(userId);
        List<String> allFriends = user.getFriends();
        return new ResponseEntity<>(allFriends, HttpStatus.OK);
    }
}
