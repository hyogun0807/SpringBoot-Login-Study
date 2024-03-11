package com.example.springbootloginstudy.presentation;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.springbootloginstudy.application.UserService;
import com.example.springbootloginstudy.dto.JoinRequest;
import com.example.springbootloginstudy.dto.LoginRequest;
import com.example.springbootloginstudy.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SecurityLoginController {

	private final UserService userService;

	@GetMapping(value = {"", "/"})
	public String home(Model model, Authentication auth) {
		model.addAttribute("loginType", "security-login");
		model.addAttribute("pageName", "Security 로그인");

		if(auth != null) {
			User loginUser = userService.getLoginUserByLoginId(auth.getName());
			if (loginUser != null) {
				model.addAttribute("nickname", loginUser.getNickname());
			}
		}
		return "home";
	}

	@GetMapping("/join")
	public String joinPage(Model model) {
		model.addAttribute("loginType", "security-login");
		model.addAttribute("pageName", "Security 로그인");

		model.addAttribute("joinRequest", new JoinRequest());
		return "join";
	}

	@PostMapping("/join")
	public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
		model.addAttribute("loginType", "security-login");
		model.addAttribute("pageName", "Security 로그인");

		// loginId 중복 체크
		if(userService.checkDuplicatedLoginId(joinRequest.getLoginId())) {
			return "join";
		}

		userService.join2(joinRequest);
		return "redirect:/security-login";
	}

	@GetMapping("/login")
	public String loginPage(Model model) {
		model.addAttribute("loginType", "security-login");
		model.addAttribute("pageName", "Security 로그인");

		model.addAttribute("loginRequest", new LoginRequest());
		return "login";
	}

	@GetMapping("/info")
	public String userInfo(Model model, Authentication auth) {
		model.addAttribute("loginType", "security-login");
		model.addAttribute("pageName", "Security 로그인");

		User loginUser = userService.getLoginUserByLoginId(auth.getName());
		model.addAttribute("user", loginUser);

		return "info";
	}

	@GetMapping("/admin")
	public String adminPage(Model model) {
		model.addAttribute("loginType", "security-login");
		model.addAttribute("pageName", "Security 로그인");

		return "admin";
	}
}
