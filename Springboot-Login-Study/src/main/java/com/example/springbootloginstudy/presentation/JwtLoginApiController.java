package com.example.springbootloginstudy.presentation;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootloginstudy.application.UserService;
import com.example.springbootloginstudy.auth.JwtTokenUtil;
import com.example.springbootloginstudy.dto.JoinRequest;
import com.example.springbootloginstudy.dto.LoginRequest;
import com.example.springbootloginstudy.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt-login")
public class JwtLoginApiController {

	private final UserService userService;

	@PostMapping("/join")
	public String join(@RequestBody JoinRequest joinRequest) {

		// loginId 중복 체크
		if(userService.checkDuplicatedLoginId(joinRequest.getLoginId())) {
			return "로그인 아이디가 중복됩니다.";
		}
		// 닉네임 중복 체크
		if(userService.checkDuplicatedNickname(joinRequest.getNickname())) {
			return "닉네임이 중복됩니다.";
		}
		// password와 passwordCheck가 같은지 체크
		if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
			return"바밀번호가 일치하지 않습니다.";
		}

		userService.join2(joinRequest);
		return "회원가입 성공";
	}

	@PostMapping("/login")
	public String login(@RequestBody LoginRequest loginRequest) {

		User user = userService.login(loginRequest);

		// 로그인 아이디나 비밀번호가 틀린 경우 global error return
		if(user == null) {
			return"로그인 아이디 또는 비밀번호가 틀렸습니다.";
		}

		// 로그인 성공 => Jwt Token 발급

		String secretKey = "my-secret-key-123123";
		long expireTimeMs = 1000 * 60 * 60;     // Token 유효 시간 = 60분

		return JwtTokenUtil.createToken(user.getLoginId(), secretKey, expireTimeMs);
	}

	@GetMapping("/info")
	public String userInfo(Authentication auth) {

		if (auth == null || !auth.isAuthenticated()) {
			return "로그인이 필요합니다.";
		}

		User loginUser = userService.getLoginUserByLoginId(auth.getName());

		if (loginUser == null) {
			return "사용자 정보를 찾을 수 없습니다.";
		}

		return String.format("loginId : %s\nnickname : %s\nrole : %s",
			loginUser.getLoginId(), loginUser.getNickname(), loginUser.getUserRole().name());
	}

	// @GetMapping("/info")
	// public String userInfo(Authentication authentication) {
	// 	if (authentication == null || !authentication.isAuthenticated()) {
	// 		return "로그인이 필요합니다.";
	// 	}
	//
	// 	User loginUser = userService.getLoginUserByLoginId(authentication.getName());
	//
	// 	if (loginUser == null) {
	// 		return "사용자 정보를 찾을 수 없습니다.";
	// 	}
	//
	// 	return String.format("loginId : %s\nnickname : %s\nrole : %s",
	// 		loginUser.getLoginId(), loginUser.getNickname(), loginUser.getUserRole().name());
	// }

	@GetMapping("/admin")
	public String adminPage() {
		return "관리자 페이지 접근 성공";
	}
}
