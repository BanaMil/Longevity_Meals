package com.capstone.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    // ✅ Base64 인코딩 제거 — 원문 문자열을 바로 HMAC 키로 사용
    private static final String SECRET = "my-very-secure-and-long-secret-key-1234567890";
    private static final long TOKEN_VALIDITY_MS = 1000L * 60 * 60 * 24; // 24시간 (기존 1시간 → 여유있게)

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String createToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + TOKEN_VALIDITY_MS);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("[JWT] 만료된 토큰: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("[JWT] 서명 불일치: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("[JWT] 검증 실패: " + e.getMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        String userId = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
