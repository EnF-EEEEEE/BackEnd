package com.enf.entity;

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

  private boolean business;      // 금전/사업

  private boolean job;           // 직장/이직

  private boolean dating;        // 연애상담

  private boolean relationship;  // 대인관계

  private boolean career;        // 취업/진로

  private boolean lifestyle;     // 일상생활

  private boolean other;          // 기타
}
