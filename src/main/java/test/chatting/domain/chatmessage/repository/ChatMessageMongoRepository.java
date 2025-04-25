package test.chatting.domain.chatmessage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import test.chatting.domain.chatmessage.document.ChatMessageDocument;


import java.util.List;
import java.util.Optional;

public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageDocument, String> {
}
