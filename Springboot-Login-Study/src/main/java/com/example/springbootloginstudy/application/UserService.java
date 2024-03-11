package com.example.springbootloginstudy.application;

import java.util.Optional;

import com.example.springbootloginstudy.config.BcryptConfig;
import com.example.springbootloginstudy.dto.LoginRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	private final BCryptPasswordEncoder encoder;

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
	// 회원가입 -> 화면에서 JoinRequest를 받아 User로 변환 후 DB에 저장
	public void join(JoinRequest joinRequest) {
		userRepository.save(joinRequest.toEntity(encoder.encode(joinRequest.getPassword())));
	}

	// 회원가입 기능 2
	// 비밀번호를 암호화 후 저장
	public void join2(JoinRequest joinRequest) {
		userRepository.save(joinRequest.toEntity(encoder.encode(joinRequest.getPassword())));
	}

	// UserId를 받아 User return -> 인증, 인가 시 사용
	// userId가 null 이거나 userId로 찾아온 user가 없으면 null return, 존재하면 User return
	public User getLoginUserById (Long userId) {
		if(userId == null) {
			return null;
		}

		Optional<User> optionalUser = userRepository.findById(userId);
		if(optionalUser.isEmpty()) {
			return null;
		}
		return optionalUser.get();
	}

	public User getLoginUserByLoginId(String loginId) {
		if(loginId == null) {
			return null;
		}

		Optional<User> optionalUser = userRepository.findByLoginId(loginId);
		if(optionalUser.isEmpty()) {
			return null;
		}
		return optionalUser.get();
	}
}
