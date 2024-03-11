package com.example.springbootloginstudy.presentation;

import com.example.springbootloginstudy.application.UserService;
import com.example.springbootloginstudy.domain.UserRole;
import com.example.springbootloginstudy.dto.JoinRequest;
import com.example.springbootloginstudy.dto.LoginRequest;
import com.example.springbootloginstudy.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cookie-login")
@Slf4j
public class CookieLoginController {

    private final UserService userService;

    @GetMapping(value = {"","/"})
    public String home(@CookieValue(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User loginUser = userService.getLoginUser(userId);

        if(loginUser != null) {
            model.addAttribute("nickname", loginUser.getNickname());
            log.info("[HOME] Login User Nickname : {}", loginUser.getNickname());
        } else {
            log.info("[HOME] Not Login User");
        }

        return "cookie-login/home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("joinForm", new JoinRequest());
        return "cookie-login/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinForm, BindingResult bindingResult) {

        // 중복 로그인 아이디 체크
        if (userService.checkDuplicatedLoginId(joinForm.getLoginId())) {
            bindingResult.addError(new FieldError("joinFrom", "loginId", "아이디가 중복됩니다."));
        }

        // 중복 닉네임 체크
        if (userService.checkDuplicatedNickname(joinForm.getNickname())) {
            bindingResult.addError(new FieldError("joinForm", "nickname", "닉네임이 중복됩니다."));
        }

        // password 체크
        if (!joinForm.getPassword().equals(joinForm.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinForm", "passwordCheck", "비밀번호가 불일치합니다."));
        }


        if (bindingResult.hasErrors()) {
            log.info("[JOIN] 회원가입 실패");
            return "cookie-login/join";
        }

        userService.join(joinForm);
        log.info("[JOIN] 회원가입 성공");
        return "redirect:/cookie-login";

    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginFrom", new LoginRequest());
        return "cookie-login/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginForm, BindingResult bindingResult
                        , HttpServletResponse response) {
        User user = userService.login(loginForm);

        if (user == null) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if (bindingResult.hasErrors()) {
            log.info("[LOGIN] 로그인 실패");
            return "cookie-login/login";
        }

        Cookie cookie = new Cookie("userId", String.valueOf(user.getId()));
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        log.info("[LOGIN] 로그인 성공 : {}", user.getNickname());
        return "redirect:/cookie-login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("userId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info("[LOGOUT] 로그아웃 성공");
        return "redirect:/cookie-login";
    }

    @GetMapping("/info")
    public String userInfo(@CookieValue(name = "userId", required = false) Long userId, Model model) {
        User loginUser = userService.getLoginUserById(userId);

        if (loginUser == null) {
            return "redirect:/cookie-login/login";
        }
        model.addAttribute("user", loginUser);
        return "cookie-login/info";
    }

    @GetMapping("/admin")
    public String adminPage(@CookieValue(name = "userId", required = false) Long userId) {
        User loginUser = userService.getLoginUserById(userId);

        if (loginUser == null) {
            return "redirect:/cookie-login/login";
        }

        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            return "redirect:/cookie-login";
        }

        return "cookie-login/admin";
    }
}
