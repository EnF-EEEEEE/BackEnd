package com.enf.config;

import com.enf.component.token.JwtUtil;
import com.enf.component.token.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenProvider tokenProvider) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)              // csrf disable
                .formLogin(AbstractHttpConfigurer::disable)     // form 로그인 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)     // http basic 인증 방식 disable
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(                         // 경로별 인가 설정
                authorizeRequests -> authorizeRequests
                                .requestMatchers(SecurityConstants.allowedUrls).permitAll()  // 허용 URL 설정
                                .requestMatchers(SecurityConstants.adminUrls).hasAnyAuthority("ADMIN","DEVELOPER") // 관리자만 접근 가능
                                .requestMatchers(SecurityConstants.userUrls).hasAnyAuthority("UNKNOWN","MENTEE","MENTOR","ADMIN","DEVELOPER") // 주니어 사용자 접근 가능
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenFilter(tokenProvider,jwtUtil), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


}
