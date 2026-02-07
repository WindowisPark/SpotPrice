package com.spotprice.api.auth;

import com.spotprice.api.dto.ApiResponse;
import com.spotprice.application.dto.command.LoginCommand;
import com.spotprice.application.dto.command.RegisterCommand;
import com.spotprice.application.dto.result.AuthResult;
import com.spotprice.application.port.in.LoginUseCase;
import com.spotprice.application.port.in.RegisterUseCase;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(RegisterUseCase registerUseCase,
                          LoginUseCase loginUseCase,
                          JwtTokenProvider jwtTokenProvider) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResult>> register(@RequestBody RegisterRequest request) {
        AuthResult result = registerUseCase.register(
                new RegisterCommand(request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResult>> login(@RequestBody LoginRequest request,
                                                         HttpServletResponse response) {
        AuthResult result = loginUseCase.login(
                new LoginCommand(request.email(), request.password()));

        String token = jwtTokenProvider.generate(result.userId());
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(3600)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // v1: 클라이언트 로그아웃 (쿠키 삭제). JWT 서버 무효화(블랙리스트)는 v2 범위.
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, null, null));
    }

    public record RegisterRequest(String email, String password) {}
    public record LoginRequest(String email, String password) {}
}
