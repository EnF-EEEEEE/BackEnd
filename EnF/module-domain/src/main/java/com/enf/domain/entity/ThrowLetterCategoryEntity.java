package com.enf.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "throw_letter_category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ThrowLetterCategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long throwLetterCategorySeq;

  private Long career;           // 커리어

  private Long mental;           // 마음건강

  private Long relationship;     // 대인관계

  private Long love;             // 사랑

  private Long life;             // 삶의 방향/가치관

  private Long finance;          // 자산관리

  private Long housing;          // 주거/독립

  private Long other;            // 기타

}
