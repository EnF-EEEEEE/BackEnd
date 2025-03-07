package com.enf.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "letter")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LetterEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long letterSeq;

  private String birdName;

  private String categoryName;

  private String letterTitle;

  private String letter;

  private LocalDateTime createAt;
}
