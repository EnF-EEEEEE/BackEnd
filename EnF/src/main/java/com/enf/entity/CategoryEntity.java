package com.enf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long categorySeq;

  @Column(nullable = false)
  private boolean career;           // 커리어

  @Column(nullable = false)
  private boolean mental;           // 마음건강

  @Column(nullable = false)
  private boolean relationship;     // 대인관계

  @Column(nullable = false)
  private boolean love;             // 사랑

  @Column(nullable = false)
  private boolean life;             // 삶의 방향/가치관

  @Column(nullable = false)
  private boolean finance;          // 자산관리

  @Column(nullable = false)
  private boolean housing;          // 주거/독립

  @Column(nullable = false)
  private boolean other;            // 기타
}
