package com.example.springbootloginstudy.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.springbootloginstudy.application.UserService;
import com.example.springbootloginstudy.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

	private final UserService userService;
	private final String secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		// Header의 Authorization값이 비어있음 -> Jwt 토큰 전송 x -> 로그인 x
		if(authorizationHeader == null) {
			filterChain.doFilter(request, response);
			System.out.println("토큰 전송x, 로그인 x");
			return;
		}

		// Header의 Authrorization 값이 'Bearer'로 시작하지 않으면 잘못된 토큰
		if(!authorizationHeader.startsWith("Bearer ")) {
			 filterChain.doFilter(request, response);
			 return;
		}

		// 전송받은 값에서 'Bearer' 뒷부분(Jwt Token) 추출
		String token = authorizationHeader.split(" ")[1];

		// 전송받은 Jwt Token이 만료되었으면 => 다음 필터 진행(인증 X)
		if(JwtTokenUtil.isExpired(token, secretKey)) {
			filterChain.doFilter(request, response);
			return;
		}

		// Jwt Token에서 loginId 추출
		String loginId = JwtTokenUtil.getLoginId(token, secretKey);

		// 추출한 loginId로 User 찾아오기
		User loginUser = userService.getLoginUserByLoginId(loginId);

		// loginUser 정보로 UsernamePasswordAuthenticationToken 발급
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			loginUser.getLoginId(), null, List.of(new SimpleGrantedAuthority(loginUser.getUserRole().name())));
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		// 권한 부여
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		filterChain.doFilter(request, response);
	}
}
