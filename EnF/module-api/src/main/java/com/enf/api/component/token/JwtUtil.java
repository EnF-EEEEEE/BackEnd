package com.enf.api.component.token;

import com.enf.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final AuthService authService;

    // UserDetails 조회 및 Authentication 객체 생성
    public Authentication getAuthentication(Long userSeq) {
        UserDetails userDetails = authService.loadUserById(userSeq);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
