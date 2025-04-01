package test.chatting.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.chatting.domain.room.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
}
