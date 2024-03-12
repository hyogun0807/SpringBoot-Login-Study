package com.example.springbootloginstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

import com.example.springbootloginstudy.auth.MyAccessDeniedHandler;
import com.example.springbootloginstudy.auth.MyAuthenticationEntryPoint;
import com.example.springbootloginstudy.auth.PrincipalOauth2UserService;
import com.example.springbootloginstudy.domain.UserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final PrincipalOauth2UserService principalOauth2UserService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeRequests()
			.requestMatchers(HttpMethod.GET,"/security-login/info").authenticated()
			.requestMatchers(HttpMethod.GET,"/security-login/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
			.anyRequest().permitAll()

			.and()
			.formLogin(
				formLogin ->
					formLogin
						.usernameParameter("loginId")
						.passwordParameter("password")
						.loginPage("/security-login/login")
						.defaultSuccessUrl("/security-login")
						.failureUrl("/security-login/login")
			)

			.logout((logout) -> logout
				.logoutSuccessUrl("/security-login/logout")
				.invalidateHttpSession(true).deleteCookies("JSESSIONID"))

			// OAuth 로그인
			.oauth2Login((oauth2) -> oauth2
				.loginPage("/security-login/login")
				.defaultSuccessUrl("/security-login/login")
				.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
					.userService(principalOauth2UserService)));

		http
			.exceptionHandling(authenticationManager -> authenticationManager
				.authenticationEntryPoint(new MyAuthenticationEntryPoint())
				.accessDeniedHandler(new MyAccessDeniedHandler()));

		return http.build();
	}
}
