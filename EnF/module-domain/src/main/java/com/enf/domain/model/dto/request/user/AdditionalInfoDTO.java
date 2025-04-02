package com.enf.domain.model.dto.request.user;

import com.enf.domain.entity.BirdEntity;
import com.enf.domain.entity.CategoryEntity;
import com.enf.domain.entity.RoleEntity;
import com.enf.domain.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AdditionalInfoDTO {

  @JsonProperty("birdName")
  private String birdName;

  @JsonProperty("nickname")
  private String nickname;

  @JsonProperty("userRole")
  private String userRole;

  @JsonProperty("userCategory")
  private UserCategoryDTO userCategory;

  @JsonCreator
  public AdditionalInfoDTO(String birdName, String nickname,
      String userRole,  UserCategoryDTO userCategory) {

    this.birdName = birdName;
    this.nickname = nickname;
    this.userRole = userRole;
    this.userCategory = userCategory;
  }


  public static UserEntity of(UserEntity user, BirdEntity bird, RoleEntity role,
      CategoryEntity category, AdditionalInfoDTO additionalInfoDTO) {

    return UserEntity.builder()
        .userSeq(user.getUserSeq())
        .bird(bird)
        .role(role)
        .category(category)
        .email(user.getEmail())
        .nickname(additionalInfoDTO.getNickname())
        .provider(user.getProvider())
        .providerId(user.getProviderId())
        .quota(role.getRoleName().equals("MENTEE") ? 4 : 7)
        .createAt(user.getCreateAt())
        .lastLoginAt(user.getLastLoginAt())
        .build();
  }
}