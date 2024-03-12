package com.example.springbootloginstudy.entity;

import com.example.springbootloginstudy.domain.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String loginId;

	private String password;

	private String nickname;

	private UserRole userRole;

	// OAuth 로그인에 사용
	private String provider;
	private String providerId;

	@Builder
	private User(String loginId, String password, String nickname, String provider, String providerId, UserRole userRole) {
		this.loginId = loginId;
		this.password = password;
		this.nickname = nickname;
		this.provider = provider;
		this.providerId = providerId;
		this.userRole = userRole;
	}
}
