package com.enf.entity;

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

  private String birdName;

  private String explanation;

  private String traits1;

  private String traits2;

  private String traits3;
}
