package test.chatting.domain.chat.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String message;

    public static ChatMessage leaveMessage(String roomId, String sender) {
        return new ChatMessage(MessageType.LEAVE, roomId, sender, sender + "님이 퇴장하셨습니다.");
    }
}