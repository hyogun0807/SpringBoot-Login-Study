package com.example.springbootloginstudy;

import com.example.springbootloginstudy.domain.UserRole;
import com.example.springbootloginstudy.entity.User;
import com.example.springbootloginstudy.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MakeInitData {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @PostConstruct
    public void makeAdminAndUser() {
        User admin1 = User.builder()
                .loginId("admin1")
                .password("1234")
                .nickname("관리자1")
                .userRole(UserRole.ADMIN)
                .build();
        userRepository.save(admin1);

        User user1 = User.builder()
                .loginId("user1")
                .password("1234")
                .nickname("USER1")
                .userRole(UserRole.USER)
                .build();
        userRepository.save(user1);

        User admin2 = User.builder()
            .loginId("admin2")
            .password(encoder.encode("1234"))
            .nickname("관리자")
            .userRole(UserRole.ADMIN)
            .build();
        userRepository.save(admin2);

        User user2 = User.builder()
            .loginId("user")
            .password(encoder.encode("1234"))
            .nickname("유저1")
            .userRole(UserRole.USER)
            .build();
        userRepository.save(user2);
    }
}
