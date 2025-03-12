package test.chatting.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import test.chatting.domain.chat.dto.ChatMessage;

public class Util {
    public static class Chat {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static TextMessage resolveTextMessage(ChatMessage message) {
            try {
                return new TextMessage(objectMapper.writeValueAsString(message));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public static ChatMessage resolvePayload(String payload) {
            try {
                return objectMapper.readValue(payload, ChatMessage.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}