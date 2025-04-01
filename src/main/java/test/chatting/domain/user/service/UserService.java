package test.chatting.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import test.chatting.domain.user.entity.User;
import test.chatting.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User loginOrRegister(String userId) {
        return userRepository.findById(userId)
                .orElseGet(() -> userRepository.save(User.of(userId)));
    }
}
