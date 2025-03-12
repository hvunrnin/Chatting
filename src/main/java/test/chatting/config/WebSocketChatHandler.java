package test.chatting.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import test.chatting.domain.chat.dto.ChatMessage;
import test.chatting.domain.chat.service.ChatService;

@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ChatService chatService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        ChatMessage chatMessage = Util.Chat.resolvePayload(payload);
        chatService.handleAction(chatMessage.getRoomId(), session, chatMessage);
    }
}