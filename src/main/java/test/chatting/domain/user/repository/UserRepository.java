package test.chatting.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.chatting.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}
