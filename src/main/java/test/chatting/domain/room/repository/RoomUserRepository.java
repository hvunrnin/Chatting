package test.chatting.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.chatting.domain.room.entity.RoomUser;

import java.util.List;

public interface RoomUserRepository extends JpaRepository<RoomUser, RoomUser.RoomUserId> {
    List<RoomUser> findAllByUserId(String userId);
}
