package com.enf.model.dto.request.user;

import com.enf.entity.CategoryEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserCategoryDTO {

  @JsonProperty("business")
  private boolean business;      // 금전/사업

  @JsonProperty("job")
  private boolean job;           // 직장/이직

  @JsonProperty("dating")
  private boolean dating;        // 연애상담

  @JsonProperty("relationship")
  private boolean relationship;  // 대인관계

  @JsonProperty("career")
  private boolean career;        // 취업/진로

  @JsonProperty("lifestyle")
  private boolean lifestyle;     // 일상생활

  @JsonProperty("other")
  private boolean other;          // 기타

  @JsonCreator
  public UserCategoryDTO(boolean business, boolean job, boolean dating,
      boolean relationship, boolean career, boolean lifestyle, boolean other) {

    this.business = business;
    this.job = job;
    this.dating = dating;
    this.relationship = relationship;
    this.career = career;
    this.lifestyle = lifestyle;
    this.other = other;
  }


  public static CategoryEntity of(UserCategoryDTO userCategoryDTO) {
    return CategoryEntity.builder()
        .business(userCategoryDTO.isBusiness())
        .job(userCategoryDTO.isJob())
        .dating(userCategoryDTO.isDating())
        .relationship(userCategoryDTO.isRelationship())
        .career(userCategoryDTO.isCareer())
        .lifestyle(userCategoryDTO.isLifestyle())
        .other(userCategoryDTO.isOther())
        .build();
  }

}
