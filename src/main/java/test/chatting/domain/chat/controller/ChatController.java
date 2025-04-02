package test.chatting.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import test.chatting.domain.chat.dto.ChatMessage;
import test.chatting.domain.chatmessage.service.ChatMessageMongoService;
import test.chatting.domain.chat.service.ChatService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageMongoService chatMessageMongoService;
    private final SimpMessagingTemplate messagingTemplate;

    // 사용자별 현재 접속한 방을 저장하는 Map
    private static final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();


    // 메시지 전송
//    @MessageMapping("/chat.sendMessage/{roomId}")
//    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
//        System.out.println("📩 서버에서 받은 메시지 (방: " + roomId + "): " + chatMessage.getMessage());
//
//        messagingTemplate.convertAndSend("/sub/chat/" + roomId, chatMessage);
//    }

    // 사용자가 입장했을 때
    @MessageMapping("/chat.addUser/{roomId}")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();

        // 사용자 세션 저장 (현재 방 업데이트)
        userSessions.put(username, roomId);

        // 입장 메시지 전송
        ChatMessage enterMessage = new ChatMessage(ChatMessage.MessageType.ENTER, roomId, username, username + "님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/sub/chat/" + roomId, enterMessage);
    }

    // 사용자가 채팅 종료 버튼을 눌렀을 때
    @MessageMapping("/chat.leaveUser/{roomId}")
    public void leaveUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        String username = chatMessage.getSender();

        // 사용자가 현재 방에 있는지 확인
        if (userSessions.containsKey(username)) {
            userSessions.remove(username); // 사용자 제거
        }

        // 퇴장 메시지 전송
        ChatMessage leaveMessage = ChatMessage.leaveMessage(roomId, username);
        messagingTemplate.convertAndSend("/sub/chat/" + roomId, leaveMessage);
    }

    @MessageMapping("/chat/message") // 클라이언트가 /pub/chat/message로 보낼 때 처리
    public void message(ChatMessage message) {
        // 1. 메시지 저장
        chatMessageMongoService.saveMessage(
                message.getRoomId(),
                message.getSender(),
                message.getMessage()
        );

        // 2. 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
