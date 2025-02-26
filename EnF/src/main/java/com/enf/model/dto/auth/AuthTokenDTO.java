package com.enf.model.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthTokenDTO {
    private String accessToken;
    private String refreshToken;
}