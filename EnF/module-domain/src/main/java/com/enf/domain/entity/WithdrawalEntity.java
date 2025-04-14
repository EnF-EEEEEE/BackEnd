package com.enf.domain.entity;

import com.enf.domain.model.type.WithdrawalType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "withdrawal")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class WithdrawalEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long withdrawalSeq;

  private String withdrawalUser;

  @Enumerated(EnumType.STRING)
  private WithdrawalType withdrawalType;

}
