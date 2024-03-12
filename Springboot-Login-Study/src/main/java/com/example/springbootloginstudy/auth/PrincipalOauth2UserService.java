package com.example.springbootloginstudy.auth;


import java.util.Optional;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.springbootloginstudy.domain.UserRole;
import com.example.springbootloginstudy.entity.User;
import com.example.springbootloginstudy.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		log.info("getAttribute : {}", oAuth2User.getAttributes());

		String provider = userRequest.getClientRegistration().getRegistrationId();
		String providerId = oAuth2User.getAttribute("sub");
		String loginId = provider + "_" + providerId;

		Optional<User> optionalUser = userRepository.findByLoginId(loginId);
		User user;

		if(optionalUser.isEmpty()) {
			user = User.builder()
				.loginId(loginId)
				.nickname(oAuth2User.getAttribute("name"))
				.provider(provider)
				.providerId(providerId)
				.userRole(UserRole.USER)
				.build();
			userRepository.save(user);
		} else {
			user = optionalUser.get();
		}

		return new PrincipalDetails(user, oAuth2User.getAttributes());
	}
}
