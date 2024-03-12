package com.example.springbootloginstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.springbootloginstudy.application.UserService;
import com.example.springbootloginstudy.auth.JwtTokenFilter;
import com.example.springbootloginstudy.domain.UserRole;

import lombok.RequiredArgsConstructor;

// @Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig2 {

	private final UserService userService;
	private static String secretKey = "my-secret-key-123123";

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		System.out.println("secretKey = " + secretKey);

		return httpSecurity
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagementConfigurer ->
				sessionManagementConfigurer.sessionCreationPolicy((SessionCreationPolicy.STATELESS)))


			.addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.requestMatchers(HttpMethod.GET, "/jwt-login/info").authenticated()
			.requestMatchers(HttpMethod.GET, "/jwt-login/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
			.and()
			.build();
	}
}
