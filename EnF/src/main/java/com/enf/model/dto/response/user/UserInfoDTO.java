package com.enf.model.dto.response.user;

import com.enf.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

  private String birdName;

  private String nickname;

  private String roleName;


  public static UserInfoDTO of(UserEntity user) {
    return new UserInfoDTO(
        user.getBird().getBirdName(),
        user.getNickname(),
        user.getRole().getRoleName());
  }

}
