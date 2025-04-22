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
import test.chatting.domain.room.entity.RoomUser;
import test.chatting.domain.room.repository.RoomUserRepository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageMongoService chatMessageMongoService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomUserRepository roomUserRepository;

    // 사용자별 현재 접속한 방을 저장하는 Map
    private static final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> userJoinedRooms = new ConcurrentHashMap<>();

    // 메시지 전송
//    @MessageMapping("/chat.sendMessage/{roomId}")
//    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
//        System.out.println("📩 서버에서 받은 메시지 (방: " + roomId + "): " + chatMessage.getMessage());
//
//        messagingTemplate.convertAndSend("/sub/chat/" + roomId, chatMessage);
//    }

    // 사용자가 입장했을 때
    @MessageMapping("/chat/addUser/{roomId}")
    public void addUser(@DestinationVariable String roomId,
                        @Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();

        // 세션 저장
        headerAccessor.getSessionAttributes().put("username", username);

        // 최초 입장인지 확인
        boolean isFirstEnter = userJoinedRooms
                .computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet())
                .add(username); // 이 때 최초 입장이면 true 반환됨


        System.out.println("!!"+isFirstEnter);


        if (isFirstEnter) {
            // 최초 입장일 때만 메시지 전송
            ChatMessage enterMessage = ChatMessage.builder()
                    .messageType(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(username)
                    .message(username + "님이 입장하셨습니다.")
                    .timestamp(Instant.now())
                    .build();

            chatMessageMongoService.saveMessage(
                    roomId,
                    username,
                    enterMessage.getMessage(),
                    enterMessage.getTimestamp()
            );

            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, enterMessage);
        }
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
        Instant now = Instant.now();

        // 1. timestamp 없으면 설정
        if (message.getTimestamp() == null) {
            message.setTimestamp(now);
        }

        // 2. 저장
        chatMessageMongoService.saveMessage(
                message.getRoomId(),
                message.getSender(),
                message.getMessage(),
                message.getTimestamp()
        );

        // 3. 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

}
