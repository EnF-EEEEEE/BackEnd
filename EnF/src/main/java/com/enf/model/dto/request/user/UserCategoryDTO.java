package com.enf.model.dto.request.user;

import com.enf.entity.CategoryEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserCategoryDTO {

  @JsonProperty("career")
  private boolean career;            // 커리어

  @JsonProperty("mental")
  private boolean mental;            // 마음건강

  @JsonProperty("relationship")
  private boolean relationship;      // 대인관계

  @JsonProperty("love")
  private boolean love;              // 사랑

  @JsonProperty("life")
  private boolean life;              // 삶의 방향/가치관

  @JsonProperty("finance")
  private boolean finance;           // 자산관리

  @JsonProperty("housing")
  private boolean housing;           // 주거/독립

  @JsonProperty("other")
  private boolean other;              // 기타

  @JsonCreator
  public UserCategoryDTO(boolean career, boolean mental, boolean relationship,
      boolean love, boolean life, boolean finance, boolean housing, boolean other) {
    this.career = career;
    this.mental = mental;
    this.relationship = relationship;
    this.love = love;
    this.life = life;
    this.finance = finance;
    this.housing = housing;
    this.other = other;
  }


  public static CategoryEntity of(UserCategoryDTO userCategoryDTO) {
    return CategoryEntity.builder()
        .career(userCategoryDTO.isCareer())
        .mental(userCategoryDTO.isMental())
        .relationship(userCategoryDTO.isRelationship())
        .love(userCategoryDTO.isLove())
        .life(userCategoryDTO.isLife())
        .finance(userCategoryDTO.isFinance())
        .housing(userCategoryDTO.isHousing())
        .other(userCategoryDTO.isOther())
        .build();
  }

  public static UserCategoryDTO of(CategoryEntity category) {
    return new UserCategoryDTO(
        category.isCareer(),
        category.isMental(),
        category.isRelationship(),
        category.isLove(),
        category.isLife(),
        category.isFinance(),
        category.isHousing(),
        category.isOther()
    );
  }
}
