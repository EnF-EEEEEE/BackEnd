package com.enf.config;

import com.enf.component.token.JwtUtil;
import com.enf.component.token.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(                         // 경로별 인가 설정
                authorizeRequests -> authorizeRequests
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**"
                    ).permitAll()

                    // Actuator 엔드포인트 허용 (이 부분이 추가됨)
                    .requestMatchers(
                            "/actuator/**",
                            "/actuator/prometheus",
                            "/actuator/health",
                            "/actuator/info"
                    ).permitAll()

                    .requestMatchers(
                        "/api/v1/auth/callback",    // kakao sns login redirect url
                        "/api/v1/auth/kakao",
                        "/api/v1/auth/reissue-token",
                        "/api/v1/user/check-nickname",
                        "/api/v1/admin/login",
                        "/api/v1/admin/callback",
                        "/api/v1/admin/callback/**",
                        "/api/v1/admin/login",
                        "/admin/login",
                        "/favicon.ico",
                        "/ut/test/send"
                    ).permitAll()

                    .requestMatchers(
                        "/api/v1/admin/dashboard",
                        "/api/v1/admin/stats",
                        "/api/v1/admin/dashboard-data",
                        "/api/v1/admin/inquiries",
                        "/api/v1/admin/inquiries/**",
                        "/admin/dashboard",
                        "/admin/inquiries",
                        "/admin/inquiries/**",
                        "/api/v1/admin/letters",
                        "/api/v1/admin/letters/**",
                        "/api/v1/admin/reports",
                        "/api/v1/admin/reports/**"
                        ).hasAnyAuthority("ADMIN", "DEVELOPER")

                    .requestMatchers(
                        "/api/v1/letter/send",
                        "/api/v1/letter/thanks"
                    ).hasAnyAuthority("MENTEE", "ADMIN", "DEVELOPER")

                    .requestMatchers(
                        "/api/v1/user/update/category",
                        "/api/v1/letter/reply",
                        "/api/v1/letter/throw"
                    ).hasAnyAuthority("MENTOR", "ADMIN", "DEVELOPER")
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtTokenFilter(tokenProvider, jwtUtil),
                UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


}
