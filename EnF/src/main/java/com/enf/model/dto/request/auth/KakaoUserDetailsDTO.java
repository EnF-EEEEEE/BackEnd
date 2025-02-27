package com.enf.model.dto.request.auth;

import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KakaoUserDetailsDTO {

  private Map<String, Object> attributes;

  public String getProviderId() {
    return attributes.get("id").toString();
  }

  public String getProvider() {
    return "kakao";
  }

  public String getEmail() {
    return (String) ((Map<?, ?>) attributes.get("kakao_account")).get("email");
  }

  public String getNickname() {
    return (String) ((Map<?, ?>) attributes.get("properties")).get("nickname");
  }

  public static UserEntity of(KakaoUserDetailsDTO userInfo, RoleEntity role) {
    LocalDateTime now = LocalDateTime.now();
    return UserEntity.builder()
            .email(userInfo.getEmail())
            .nickname(userInfo.getNickname())
            .providerId(userInfo.getProviderId())
            .provider(userInfo.getProvider())
            .createAt(now)
            .lastLoginAt(now)
            .role(role)
            .build();
  }
}
