package com.capstone.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // CORS preflight는 바로 패스
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        final String uri = request.getRequestURI();
        final String header = request.getHeader("Authorization");
        log.debug("[JWT] {} {} | Authorization={}", request.getMethod(), uri, header);

        try {
            // 헤더 형식 점검
            if (header != null && header.startsWith("Bearer ")) {
                final String token = header.substring(7).trim();
                if (token.isEmpty()) {
                    log.warn("[JWT] Bearer 토큰이 비어 있음");
                    SecurityContextHolder.clearContext();
                } else if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("[JWT] 인증 성공 userId={}", auth.getName());
                } else {
                    log.warn("[JWT] 토큰 검증 실패 (만료/서명 오류 등)");
                    SecurityContextHolder.clearContext();
                }
            } else {
                // 헤더가 없거나 Bearer 형식이 아님 → 익명으로 통과
                log.debug("[JWT] Authorization 헤더 없음 또는 형식 불일치 (익명 접근)");
            }
        } catch (Exception e) {
            // 어떤 예외든 컨텍스트를 깨끗이 하고 다음 필터로 넘김
            log.error("[JWT] 인증 처리 중 예외 발생: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
