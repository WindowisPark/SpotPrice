package com.spotprice.api.web;

import com.spotprice.api.auth.JwtTokenProvider;
import com.spotprice.application.dto.command.LoginCommand;
import com.spotprice.application.dto.command.RegisterCommand;
import com.spotprice.application.dto.result.AuthResult;
import com.spotprice.application.port.in.LoginUseCase;
import com.spotprice.application.port.in.RegisterUseCase;
import com.spotprice.domain.exception.AuthenticationFailedException;
import com.spotprice.domain.exception.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebAuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public WebAuthController(LoginUseCase loginUseCase,
                             RegisterUseCase registerUseCase,
                             JwtTokenProvider jwtTokenProvider) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response,
                        Model model) {
        try {
            AuthResult result = loginUseCase.login(new LoginCommand(email, password));
            setTokenCookie(response, result.userId());
            return "redirect:/";
        } catch (AuthenticationFailedException e) {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           HttpServletResponse response,
                           Model model) {
        try {
            AuthResult result = registerUseCase.register(new RegisterCommand(email, password));
            setTokenCookie(response, result.userId());
            return "redirect:/";
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return "redirect:/";
    }

    private void setTokenCookie(HttpServletResponse response, Long userId) {
        String token = jwtTokenProvider.generate(userId);
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(3600)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
