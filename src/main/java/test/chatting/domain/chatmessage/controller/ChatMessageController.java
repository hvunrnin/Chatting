package test.chatting.domain.chatmessage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.chatting.domain.chatmessage.document.ChatMessageDocument.Message;
import test.chatting.domain.chatmessage.dto.ChatMessageDTO;
import test.chatting.domain.chatmessage.repository.ChatMessageMongoRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageMongoRepository chatMessageMongoRepository;

    @GetMapping("/history")
    public List<ChatMessageDTO> getRecentMessages(@RequestParam String roomId) {
        return chatMessageMongoRepository.findById(roomId)
                .map(doc -> doc.getMessagesByDate().values().stream()
                        .flatMap(List::stream)
                        .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                        .map(m -> ChatMessageDTO.builder()
                                .sender(m.getSender())
                                .message(m.getMessage())
                                .timestamp(m.getTimestamp())
                                .build())
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

}

