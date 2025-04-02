package com.enf.domain.model.dto.response.letter;

import com.enf.domain.entity.ThrowLetterCategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ThrowLetterCategoryDTO {

  private Long career;           // 커리어

  private Long mental;           // 마음건강

  private Long relationship;     // 대인관계

  private Long love;             // 사랑

  private Long life;             // 삶의 방향/가치관

  private Long finance;          // 자산관리

  private Long housing;          // 주거/독립

  private Long other;            // 기타


  public static ThrowLetterCategoryEntity create() {
    return ThrowLetterCategoryEntity.builder()
        .career(0L)
        .mental(0L)
        .relationship(0L)
        .love(0L)
        .life(0L)
        .finance(0L)
        .housing(0L)
        .other(0L)
        .build();
  }

  public static ThrowLetterCategoryDTO of(ThrowLetterCategoryEntity throwLetterCategoryEntity) {
    return new ThrowLetterCategoryDTO(
        throwLetterCategoryEntity.getCareer(),
        throwLetterCategoryEntity.getMental(),
        throwLetterCategoryEntity.getRelationship(),
        throwLetterCategoryEntity.getLove(),
        throwLetterCategoryEntity.getLife(),
        throwLetterCategoryEntity.getFinance(),
        throwLetterCategoryEntity.getHousing(),
        throwLetterCategoryEntity.getOther()
    );
  }

}
