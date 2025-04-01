package test.chatting.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.chatting.domain.user.entity.User;
import test.chatting.domain.user.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public User login(@RequestParam String userId) {
        return userService.loginOrRegister(userId);
    }
}
