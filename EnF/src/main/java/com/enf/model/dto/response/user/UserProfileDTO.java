package com.enf.model.dto.response.user;

import com.enf.entity.UserEntity;
import com.enf.model.dto.request.user.UserCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

  private String birdName;

  private String nickname;

  private String roleName;

  private UserCategoryDTO userCategory;


  public static UserProfileDTO of(UserEntity user) {
    return new UserProfileDTO(
        user.getBird().getBirdName(),
        user.getNickname(),
        user.getRole().getRoleName(),
        user.getRole().getRoleName().equals("MENTOR")
            ? UserCategoryDTO.of(user.getCategory())
            : null);

  }

}
