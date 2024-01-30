package com.ashish.sabiniyahub.repository;

import com.ashish.sabiniyahub.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findAllBySenderIdAndRecipientId(String senderId, String recipientId);


}
