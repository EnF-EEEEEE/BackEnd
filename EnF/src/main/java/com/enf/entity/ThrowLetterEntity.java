package com.enf.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "throw_letter")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ThrowLetterEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long throwLetterSeq;

  @ManyToOne
  @JoinColumn(name = "throw_user_seq")
  private UserEntity throwUser;

  @ManyToOne
  @JoinColumn(name = "letter_status_seq")
  private LetterStatusEntity letterStatus;
}

