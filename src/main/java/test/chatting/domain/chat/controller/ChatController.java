package test.chatting.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.chatting.domain.chat.dto.ChatRoom;
import test.chatting.domain.chat.service.ChatService;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/create")
    public ChatRoom createRoom(@RequestParam String name, @RequestParam String owner) {
        return chatService.createRoom(name, owner);
    }

    @GetMapping
    public List<ChatRoom> getAll() {
        return chatService.findAll();
    }
}