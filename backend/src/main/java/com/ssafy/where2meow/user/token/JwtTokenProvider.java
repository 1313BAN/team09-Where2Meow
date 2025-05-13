package com.ssafy.where2meow.user.token;

import com.ssafy.where2meow.user.token.TokenBlacklist;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds; // 기본값 24시간
    
    // 자동 로그인용 토큰 유효 기간 (기본값 7일)
    @Value("${jwt.remember-me-validity-in-seconds:604800}")
    private long rememberMeTokenValidityInSeconds;

    private final UserDetailsService userDetailsService;
    private final TokenBlacklist tokenBlacklist;

    public JwtTokenProvider(UserDetailsService userDetailsService, TokenBlacklist tokenBlacklist) {
        this.userDetailsService = userDetailsService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 생성
    public String createToken(String email, String role) {
        return createToken(email, role, false); // 기본값은 rememberMe = false
    }
    
    // JWT 토큰 생성 (rememberMe 옵션 포함)
    public String createToken(String email, String role, boolean rememberMe) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);
        claims.put("role", role);

        Date now = new Date();
        // rememberMe가 true인 경우 더 긴 토큰 유효 기간 설정 (7일)
        long validityPeriod = rememberMe 
            ? rememberMeTokenValidityInSeconds 
            : tokenValidityInSeconds;
            
        Date validity = new Date(now.getTime() + validityPeriod * 1000);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 만료 시간 조회
    public long getExpirationTime(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.getTime();
    }

    // Request의 Header에서 token 가져오기
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 블랙리스트에 추가 (로그아웃)
    public void blacklistToken(String token) {
        try {
            long expirationTime = getExpirationTime(token);
            tokenBlacklist.addToBlacklist(token, expirationTime);
        } catch (Exception e) {
            log.error("[JWT 블랙리스트] 토큰 추가 실패: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.isBlacklisted(token);
    }
    
    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            // 블랙리스트에 있는 토큰인지 확인
            if (tokenBlacklist.isBlacklisted(token)) {
                log.warn("Blacklisted token: {}...", token.substring(0, Math.min(10, token.length())));
                return false;
            }

            Jws<Claims> claims = Jwts.parser().setSigningKey(key).build().parseSignedClaims(token);
            boolean isValid = !claims.getPayload().getExpiration().before(new Date());
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
