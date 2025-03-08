package com.enf.model.dto.response.user;

import com.enf.entity.UserEntity;
import com.enf.model.dto.request.user.AdditionalInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

  private String roleName;

  private String birdName;

  private String nickname;

  private int quota;

  private boolean isRead;


  public static UserInfoDTO of(AdditionalInfoDTO additionalInfo) {
    return new UserInfoDTO(
        additionalInfo.getUserRole(),
        additionalInfo.getBirdName(),
        additionalInfo.getNickname(),
        additionalInfo.getUserRole().equals("MENTEE") ? 4 : 7,
        false
    );
  }

  public static UserInfoDTO of(UserEntity user, int quota, boolean isRead) {
    return new UserInfoDTO(
        user.getRole().getRoleName(),
        user.getBird().getBirdName(),
        user.getNickname(),
        quota,
        isRead
    );
  }

}
