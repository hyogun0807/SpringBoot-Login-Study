package com.example.springbootloginstudy.dto;

import com.example.springbootloginstudy.domain.UserRole;
import com.example.springbootloginstudy.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

	private String loginId;
	private String password;
	private String nickname;
	private String passwordCheck;

	// 비밀번호 미암호화
	public User toEntity() {
		return User.builder()
			.loginId(this.loginId)
			.password(this.password)
			.nickname(this.nickname)
			.userRole(UserRole.USER)
			.build();
	}

	// 비밀번호 암호화
	public User toEntity(String encodedPassword) {
		return User.builder()
			.loginId(this.loginId)
			.password(encodedPassword)
			.nickname(this.nickname)
			.userRole(UserRole.USER)
			.build();
	}
}
