package com.example.springbootloginstudy.presentation;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.springbootloginstudy.application.UserService;
import com.example.springbootloginstudy.domain.UserRole;
import com.example.springbootloginstudy.dto.JoinRequest;
import com.example.springbootloginstudy.dto.LoginRequest;
import com.example.springbootloginstudy.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class SessionLoginController {

	private final UserService userService;

	@GetMapping(value = {"", "/"})
	public String home(Model model, @SessionAttribute(name = "userId", required = false) Long userId) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");

		User loginUser = userService.getLoginUser(userId);

		if (loginUser != null) {
			model.addAttribute("nickname", ((User)loginUser).getNickname());
		}
		return "home";
	}

	@GetMapping("/join")
	public String joinPage(Model model) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");
		model.addAttribute("joinRequest", new JoinRequest());
		return "join";
	}

	@PostMapping("/join")
	public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");

		// loginId 중복 체크
		if(userService.checkDuplicatedLoginId(joinRequest.getLoginId())) {
			bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
		}
		// 닉네임 중복 체크
		if(userService.checkDuplicatedNickname(joinRequest.getNickname())) {
			bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
		}
		// password와 passwordCheck가 같은지 체크
		if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
			bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "바밀번호가 일치하지 않습니다."));
		}

		if(bindingResult.hasErrors()) {
			return "join";
		}

		userService.join(joinRequest);
		return "redirect:/session-login";
	}

	@GetMapping("/login")
	public String loginPage(Model model) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");

		model.addAttribute("loginRequest", new LoginRequest());
		return "login";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult,
		HttpServletRequest httpServletRequest, Model model) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");

		User user = userService.login(loginRequest);

		// 로그인 아이디나 비밀번호가 틀린 경우 global error return
		if(user == null) {
			bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
		}

		if(bindingResult.hasErrors()) {
			return "login";
		}

		// 로그인에 성공하면 기존 세션은 파괴하고 새로운 세션 생성
		httpServletRequest.getSession().invalidate();
		HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성
		// 세션에 userId를 넣어줌
		session.setAttribute("userId", user.getId());
		session.setMaxInactiveInterval(60 * 30); // 30분

		sessionList.put(session.getId(), session);

		return "redirect:/session-login";
	}


	@GetMapping("/logout")
	public String logout(HttpServletRequest request, Model model) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");

		HttpSession session = request.getSession(false);
		if(session != null) {
			sessionList.remove(session.getId());
			session.invalidate();
		}
		return "redirect:/session-login";
	}

	@GetMapping("/info")
	public String userInfo(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");

		User loginUser = userService.getLoginUserById(userId);

		if(loginUser == null) {
			return "redirect:/session-login/login";
		}

		model.addAttribute("user", loginUser);
		return "info";
	}

	@GetMapping("/admin")
	public String adminPage(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
		model.addAttribute("loginType", "session-login");
		model.addAttribute("pageName", "세션 로그인");


		User loginUser = userService.getLoginUserById(userId);

		if(loginUser == null) {
			return "redirect:/session-login/login";
		}

		if(!loginUser.getUserRole().equals(UserRole.ADMIN)) {
			return "redirect:/session-login";
		}

		return "admin";
	}

	// 세션 리스트 확인용 코드
	public static Hashtable sessionList = new Hashtable();

	@GetMapping("/session-list")
	@ResponseBody
	public Map<String, String> sessionList() {
		Enumeration elements = sessionList.elements();
		Map<String, String> lists = new HashMap<>();
		while(elements.hasMoreElements()) {
			HttpSession session = (HttpSession)elements.nextElement();
			lists.put(session.getId(), String.valueOf(session.getAttribute("userId")));
		}
		return lists;
	}

}

