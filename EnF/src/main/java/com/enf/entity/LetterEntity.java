package com.enf.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "letter")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LetterEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long letterSeq;

  private String categoryName;

  private String letterTitle;

  private String letter;

  private LocalDateTime createAt;
}
