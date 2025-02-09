package com.enf.model.dto.request.auth;

import com.enf.Entity.UserEntity;
import com.enf.model.type.GenerateType;
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
  public String getBirthyear() {
    return (String) ((Map<?, ?>) attributes.get("kakao_account")).get("birthyear");
  }

  public String getNickname() {
    return (String) ((Map<?, ?>) attributes.get("properties")).get("nickname");
  }

  public static UserEntity of(KakaoUserDetailsDTO userInfo) {
    return UserEntity.builder()
        .email(userInfo.getEmail())
        .nickname(userInfo.getNickname())
        .providerId(userInfo.getProviderId())
        .provider(userInfo.getProvider())
        .generateType(Integer.parseInt(userInfo.getBirthyear()) >= 40
            ? GenerateType.SENIOR
            : GenerateType.YOUTH)
        .build();
  }
}
