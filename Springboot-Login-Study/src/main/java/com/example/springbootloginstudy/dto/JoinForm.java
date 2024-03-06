package com.example.springbootloginstudy.dto;

import com.example.springbootloginstudy.domain.UserRole;
import com.example.springbootloginstudy.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinForm {

	private String loginId;
	private String password;
	private String nickname;

	public User toEntity() {
		return User.builder()
			.loginId(this.loginId)
			.password(this.password)
			.nickname(this.nickname)
			.userRole(UserRole.USER)
			.build();
	}
}
