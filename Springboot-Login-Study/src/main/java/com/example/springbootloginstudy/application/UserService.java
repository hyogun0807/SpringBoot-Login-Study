package com.example.springbootloginstudy.application;

import java.util.Optional;

import com.example.springbootloginstudy.dto.LoginRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbootloginstudy.dto.JoinRequest;
import com.example.springbootloginstudy.entity.User;
import com.example.springbootloginstudy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final UserRepository userRepository;

	// 중복 loginId 체크
	public boolean checkDuplicatedLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}

	// 중복 nickname 체크
	public boolean checkDuplicatedNickname(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

	// 로그인 기능 구현
	public User login(LoginRequest loginForm) {
		Optional<User> optionalUser = userRepository.findByLoginId(loginForm.getLoginId());

		if(optionalUser.isEmpty()) {
			return null;
		}

		User user = optionalUser.get();

		if(!user.getPassword().equals(loginForm.getPassword())) {
			return null;
		}
		return user;
	}

	// 회원가입 기능
	public void join(JoinRequest joinForm) {
		userRepository.save(joinForm.toEntity());
	}

	// UserId를 받아 User return -> 인증, 인가 시 사용
	public User getLoginUser(Long userId) {
		if(userId == null) {
			return null;
		}

		Optional<User> optionalUser = userRepository.findById(userId);
		if(optionalUser.isEmpty()) {
			return null;
		}
		return optionalUser.get();
	}
}
