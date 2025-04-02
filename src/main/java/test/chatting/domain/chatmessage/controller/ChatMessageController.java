package test.chatting.domain.chatmessage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.chatting.domain.chatmessage.document.ChatMessageDocument.Message;
import test.chatting.domain.chatmessage.repository.ChatMessageMongoRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageMongoRepository chatMessageMongoRepository;

    @GetMapping("/history")
    public List<Message> getRecentMessages(@RequestParam String roomId) {
        return chatMessageMongoRepository.findAllByRoomIdOrderByChunkIdDesc(roomId).stream()
                .flatMap(doc -> doc.getMessages().stream())
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .collect(Collectors.toList());
    }
}

