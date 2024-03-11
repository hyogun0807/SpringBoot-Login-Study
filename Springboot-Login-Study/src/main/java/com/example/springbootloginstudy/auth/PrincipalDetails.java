package com.example.springbootloginstudy.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.springbootloginstudy.entity.User;

public class PrincipalDetails implements UserDetails {

	private User user;

	public PrincipalDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collections = new ArrayList<>();
		collections.add(() -> {
			return user.getUserRole().name();
		});

		return collections;
	}

	// get password 메서드
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	// get Username 메서드(생성한 User는 loginId 사용)
	@Override
	public String getUsername() {
		return user.getLoginId();
	}

	// 계정만료 확인 (true : 만료x)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정 잠긴지 확인 (true : 잠김 x)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 비밀번호 만료 확인 (true : 만료 x)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정 활성화 가능한지 (true : 활성화)
	@Override
	public boolean isEnabled() {
		return true;
	}
}
