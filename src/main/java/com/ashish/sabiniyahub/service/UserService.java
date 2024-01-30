package com.ashish.sabiniyahub.service;

import com.ashish.sabiniyahub.exception.UserNotFoundException;
import com.ashish.sabiniyahub.model.Message;
import com.ashish.sabiniyahub.model.User;
import com.ashish.sabiniyahub.repository.UserRepository;
import com.ashish.sabiniyahub.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private CloudinaryImageService cloudinaryImageService;


    public List<User> searchUsers(String search, String id) {
        Query dynamicQuery = new Query();
        if (search != null) {
            Criteria keywordCriteria = Criteria.where("name").regex(search, "i");
            dynamicQuery.addCriteria(keywordCriteria);
        }
        List<User> users = mongoTemplate.find(dynamicQuery, User.class);
        users.removeIf(u -> u.getId().equals(id));
        return users;
    }


    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No User found with the given Email: " + email));

        return user;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    public User findById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No User found with the given ID: " + id));

        return user;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public String loginUser(String email, String password) throws BadCredentialsException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return jwt;
    }


    public String processImage(String base64, String folder) {
        byte[] data = DatatypeConverter.parseBase64Binary(base64.split(",")[1]);
        Map<String, String> imageData = this.cloudinaryImageService.upload(data, "Sabiniyahub/"+folder);
        return imageData.get("secure_url");
    }


    public User getUserFromCookie(String cookie) {
        String id = jwtUtils.getIdFromJwtToken(cookie);
        User user = userRepository.findById(id).get();
        return user;
    }


    public User getUserFromJwt(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token ="";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            throw new RuntimeException("Invalid Jwt Token");
        }

        User user = findById(jwtUtils.getIdFromJwtToken(token));
        return user;
    }

}

