package com.spotprice.api.config;

import com.spotprice.api.auth.JwtAuthenticationFilter;
import com.spotprice.api.auth.JwtTokenProvider;
import com.spotprice.api.dto.ApiResponse;
import com.spotprice.api.dto.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * v1 보안 전제:
 * - Cookie 인증: API와 프론트가 동일 오리진(또는 서브도메인)에서 서빙됨을 전제.
 *   cross-origin 시나리오가 필요하면 v2에서 Bearer 토큰 또는 CORS+withCredentials 전환.
 * - CSRF: stateless JWT + SameSite=Lax 쿠키로 방어. 별도 CSRF 토큰 미사용.
 *   SameSite=Lax는 cross-site POST를 차단하므로 일반적 CSRF 공격 방어.
 *   동일 사이트 내 서브도메인 공격은 Secure 플래그 + HTTPS로 완화.
 * - 401 (UNAUTHORIZED): 토큰 없음 / 만료 / 변조 → 인증 자체 실패
 * - 403 (FORBIDDEN): 인증됐으나 해당 리소스 접근 권한 없음 (v2 역할 기반 확장 시 활용)
 */
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/offers/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/orders/**", "/api/auth/logout").authenticated()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            ApiResponse<Void> body = ApiResponse.error(ErrorCode.UNAUTHORIZED);
                            response.getWriter().write(objectMapper.writeValueAsString(body));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            ApiResponse<Void> body = ApiResponse.error(ErrorCode.FORBIDDEN);
                            response.getWriter().write(objectMapper.writeValueAsString(body));
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
