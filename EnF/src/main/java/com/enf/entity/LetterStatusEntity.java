package com.enf.entity;

import jakarta.persistence.Column;
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

@Entity(name = "letter_status")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LetterStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long letterStatusSeq;

  @ManyToOne
  @JoinColumn(name = "mentee_seq")
  private UserEntity mentee;

  @ManyToOne
  @JoinColumn(name = "mentor_seq")
  private UserEntity mentor;

  @ManyToOne
  @JoinColumn(name = "mentee_letter_seq")
  private LetterEntity menteeLetter;

  @ManyToOne
  @JoinColumn(name = "mentor_letter_seq")
  private LetterEntity mentorLetter;

  @Column(name = "is_mentee_read")
  private boolean isMenteeRead;

  @Column(name = "is_mentor_read")
  private boolean isMentorRead;

  @Column(name = "is_mentee_saved")
  private boolean isMenteeSaved;

  @Column(name = "is_mentor_saved")
  private boolean isMentorSaved;

  @Column(name = "is_thanks_to_mentor")
  private boolean isThanksToMentor;

  private LocalDateTime createAt;
}
