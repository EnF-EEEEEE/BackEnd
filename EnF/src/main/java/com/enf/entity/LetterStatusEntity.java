package com.enf.entity;

import com.enf.model.type.ThanksType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private boolean isMenteeRead = false;  // 기본값 설정

    @Column(name = "is_mentor_read")
    private boolean isMentorRead = false;  // 기본값 설정

    @Column(name = "is_mentee_saved")
    private boolean isMenteeSaved = false;  // 기본값 설정

    @Column(name = "is_mentor_saved")
    private boolean isMentorSaved = false;  // 기본값 설정

    @Enumerated(EnumType.STRING)
    @Column(name = "thanks_type")
    private ThanksType thanksType;

    private LocalDateTime createAt;

    public static LetterStatusEntity of(LetterEntity menteeLetter, UserEntity mentee, UserEntity mentor) {
      return LetterStatusEntity.builder()
          .mentee(mentee)
          .mentor(mentor)
          .menteeLetter(menteeLetter)
          .isMenteeRead(false)
          .isMentorRead(false)
          .isMenteeSaved(false)
          .isMentorSaved(false)
          .thanksType(null)
          .createAt(LocalDateTime.now())
          .build();
    }
}
