package com.enf.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "bird")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BirdEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long birdSeq;

  @Column(nullable = false)
  private String birdName;

  @Column(nullable = false)
  private String traits;

  @Column(nullable = false)
  private String myPageBirdy;

  @Column(nullable = false)
  private String testBirdy;

  @Column(nullable = false)
  private String letterBirdy;
}
