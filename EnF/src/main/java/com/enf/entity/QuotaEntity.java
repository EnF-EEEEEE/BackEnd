package com.enf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "quota")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class QuotaEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long quotaSeq;

  @OneToOne
  @JoinColumn(name = "user_seq", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  private int quota;

}
