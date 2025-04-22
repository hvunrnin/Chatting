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
import test.chatting.domain.chatmessage.service.ChatMessageMongoService;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

    private final ChatRepository chatRepository; // WebSocket 세션 저장용이므로 유지
    private final ChatMessageMongoService chatMessageMongoService;

    public ChatRoom findRoomById(String roomId) {
        return chatRepository.findById(roomId);
    }

    public void handleAction(
            String roomId,
            WebSocketSession session,
            ChatMessage chatMessage
    ) {
        ChatRoom room = findRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("존재하지 않는 채팅방입니다: " + roomId);
        }

        if (isEnterRoom(chatMessage)) {
            room.join(session);
            chatMessage.setMessage(chatMessage.getSender() + "님 환영합니다.");
        } else {
            // 채팅 메시지를 MongoDB에 저장
            chatMessageMongoService.saveMessage(
                    roomId,
                    chatMessage.getSender(),
                    chatMessage.getMessage(),
                    chatMessage.getTimestamp() != null
                            ? chatMessage.getTimestamp().atZone(java.time.ZoneOffset.UTC).toInstant()
                            : java.time.Instant.now()
            );
        }

        TextMessage textMessage = Util.Chat.resolveTextMessage(chatMessage);
        room.sendMessage(textMessage);
    }

    private boolean isEnterRoom(ChatMessage chatMessage) {
        return chatMessage.getMessageType().equals(ChatMessage.MessageType.ENTER);
    }
}
