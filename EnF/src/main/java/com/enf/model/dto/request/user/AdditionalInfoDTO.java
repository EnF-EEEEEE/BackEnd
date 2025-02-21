package com.enf.model.dto.request.user;

import com.enf.entity.BirdEntity;
import com.enf.entity.CategoryEntity;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AdditionalInfoDTO {

  @JsonProperty("birdName")
  private String birdName;

  @JsonProperty("nickname")
  private String nickname;

  @JsonProperty("birth")
  private int birth;

  @JsonProperty("ageGroup")
  private String ageGroup;

  @JsonProperty("userCategory")
  private UserCategoryDTO userCategory;

  @JsonCreator
  public AdditionalInfoDTO(String birdName, String nickname,
      int birth, String ageGroup, UserCategoryDTO userCategory) {

    this.birdName = birdName;
    this.nickname = nickname;
    this.birth = birth;
    this.ageGroup = ageGroup;
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
        .birth(additionalInfoDTO.getBirth())
        .provider(user.getProvider())
        .providerId(user.getProviderId())
        .createAt(user.getCreateAt())
        .lastLoginAt(user.getLastLoginAt())
        .build();
  }
}