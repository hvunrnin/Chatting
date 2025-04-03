package test.chatting.domain.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.chatting.domain.room.entity.ChatRoom;
import test.chatting.domain.room.entity.RoomUser;
import test.chatting.domain.room.repository.RoomUserRepository;
import test.chatting.domain.room.service.ChatRoomService;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final ChatRoomService chatRoomService;
    private final RoomUserRepository roomUserRepository;

    // 해당 roomId가 존재하면 채팅방 참여, 존재하지 않으면 채팅방 생성
    @PostMapping("/join-or-create")
    public void joinRoom(
            @RequestParam String roomId,
            @RequestParam String userId
    ) {
        chatRoomService.joinRoom(roomId, userId);
    }

    // 참여 중인 채팅방 목록
    @GetMapping("/list")
    public List<ChatRoom> getMyRooms(@RequestParam String userId) {
        return chatRoomService.getRoomsByUser(userId);
    }

    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveRoom(@RequestParam String roomId, @RequestParam String userId) {
        roomUserRepository.deleteById(new RoomUser.RoomUserId(roomId, userId));
        return ResponseEntity.ok().build();
    }
}
