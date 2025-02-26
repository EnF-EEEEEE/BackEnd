package com.enf.component.token;

import com.enf.model.dto.auth.AuthTokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class TokenProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public TokenProvider(
            @Value("${spring.jwt.secret}") String secretKey,
            @Value("${spring.jwt.access-token-validity}") long accessTokenValidity,
            @Value("${spring.jwt.refresh-token-validity}") long refreshTokenValidity) {

        // Base64 디코딩 후 Key 생성
        this.key = createKeyFromSecret(secretKey);
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    private Key createKeyFromSecret(String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);  // Base64 디코딩
        return Keys.hmacShaKeyFor(decodedKey);  // Secret Key 생성
    }

    public AuthTokenDTO generateAuthToken(Long userSeq, String role) {
        String accessToken = generateAccessToken(userSeq,role);
        String refreshToken = generateRefreshToken(userSeq,role);
        return AuthTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Access Token 생성
    public String generateAccessToken(Long userSeq, String role) {
        return Jwts.builder()
                .setClaims(Map.of("userSeq", userSeq, "role", role))                    // userSeq & role 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(Long userSeq, String role) {
        return Jwts.builder()
                .setClaims(Map.of("userSeq", userSeq,"role", role))                               // userSeq만 포함
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 검증 및 파싱
    public Long getUserSeqFromToken(String token) {
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userSeq", Long.class);
    }

    public String getUserRoleFromToken(String token) {
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 서명 검증을 위한 키 설정
                    .build()
                    .parseClaimsJws(token); //jwt 파싱 및 검증
            return true;
        }catch (ExpiredJwtException e) {
            log.info("expired token - ExpiredJwtException");
        } catch (JwtException | IllegalArgumentException e) {
            log.info("invalid token - JwtException");
        }
        return false;
    }
}
