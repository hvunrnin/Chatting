package test.chatting.domain.chatmessage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import test.chatting.domain.chatmessage.document.ChatMessageDocument;
import test.chatting.domain.chatmessage.document.ChatMessageDocument.Message;
import test.chatting.domain.chatmessage.repository.ChatMessageMongoRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ChatMessageMongoService {

    private final ChatMessageMongoRepository chatMessageMongoRepository;

    public void saveMessage(String roomId, String sender, String content) {
        String chunkId = LocalDate.now(ZoneOffset.UTC).toString(); // 예: 2025-04-02

        Message newMessage = Message.builder()
                .sender(sender)
                .message(content)
                .timestamp(Instant.now())
                .build();

        ChatMessageDocument document = chatMessageMongoRepository
                .findByRoomIdAndChunkId(roomId, chunkId)
                .orElseGet(() -> ChatMessageDocument.builder()
                        .roomId(roomId)
                        .chunkId(chunkId)
                        .messages(new ArrayList<>())
                        .build()
                );

        document.getMessages().add(newMessage);
        chatMessageMongoRepository.save(document);
    }
}
