package com.enf.domain.entity;

import jakarta.persistence.Column;
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

  @Column(nullable = false)
  private String birdName;

  @Column(nullable = false)
  private String categoryName;

  @Column(nullable = false)
  private String letterTitle;

  @Column(length = 1000, nullable = false)
  private String letter;

  @Column(nullable = false)
  private LocalDateTime createAt;
}
