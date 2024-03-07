package com.example.springbootloginstudy;

import com.example.springbootloginstudy.domain.UserRole;
import com.example.springbootloginstudy.entity.User;
import com.example.springbootloginstudy.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MakeInitData {

    private final UserRepository userRepository;

    @PostConstruct
    public void makeAdminAndUser() {
        User admin = User.builder()
                .loginId("admin")
                .password("0000")
                .nickname("관리자")
                .userRole(UserRole.ADMIN)
                .build();

        User user = User.builder()
                .loginId("user")
                .password("1111")
                .nickname("USER1")
                .userRole(UserRole.USER)
                .build();
    }
}
