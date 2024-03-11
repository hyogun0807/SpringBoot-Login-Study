package com.example.springbootloginstudy.config;

import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.example.springbootloginstudy.domain.UserRole;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			// .authorizeHttpRequests(requests -> requests
			// 	.requestMatchers(HttpMethod.GET,"/security-login/info").authenticated()
			// 	.requestMatchers(HttpMethod.GET,"/security-login/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
			// 	.anyRequest().permitAll())
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
				.invalidateHttpSession(true).deleteCookies());

		return http.build();
	}
}

// antMatcher -> requestMatcher
