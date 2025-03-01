package com.enf.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne
  @JoinColumn(name = "mentee_seq")
  private UserEntity mentee;

  @ManyToOne
  @JoinColumn(name = "mentor_seq")
  private UserEntity mentor;

  private String categoryName;

  private String letterTitle;

  private String letter;

  private LocalDateTime createAt;

  @ManyToOne
  @JoinColumn(name = "reply_to")
  private LetterEntity replyTo;
}
