package com.enf.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "tips")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TipsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tipSeq;

  private String type;

  private String tip;
}
