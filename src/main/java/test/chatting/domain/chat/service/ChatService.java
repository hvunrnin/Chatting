package test.chatting.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import test.chatting.config.Util;
import test.chatting.domain.chat.dto.ChatMessage;
import test.chatting.domain.chat.dto.ChatRoom;
import test.chatting.domain.chat.repository.ChatRepository;

import java.util.*;

import jakarta.annotation.PostConstruct;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private String defaultRoomId;

    public List<ChatRoom> findAll() {
        return chatRepository.findAll();
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRepository.findById(roomId);
    }

    public ChatRoom createRoom(String name, String owner) {
        String roomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.of(roomId, name, owner);
        chatRepository.save(roomId, chatRoom);
        return chatRoom;
    }


    public void handleAction(
            String roomId,
            WebSocketSession session,
            ChatMessage chatMessage
    ) {
        if (roomId == null || roomId.isEmpty()) {
            roomId = defaultRoomId; // 기본 채팅방 ID로 변경
        }

        ChatRoom room = findRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("존재하지 않는 채팅방입니다: " + roomId);
        }

        if (isEnterRoom(chatMessage)) {
            room.join(session);
            chatMessage.setMessage(chatMessage.getSender() + "님 환영합니다.");
        }

        TextMessage textMessage = Util.Chat.resolveTextMessage(chatMessage);
        room.sendMessage(textMessage);
    }

    private boolean isEnterRoom(ChatMessage chatMessage) {
        return chatMessage.getMessageType().equals(ChatMessage.MessageType.ENTER);
    }
}
