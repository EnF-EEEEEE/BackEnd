package com.enf.ut;

import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.repository.LetterRepository;
import com.enf.repository.LetterStatusRepository;
import com.enf.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UTService {

  private final LetterStatusRepository letterStatusRepository;
  private final LetterRepository letterRepository;
  private final UserRepository userRepository;

  public String sendLetterToMentor(UTDTO utdto) {
    UserEntity mentor = userRepository.findByNickname(utdto.getNickname());
    UserEntity mentee = userRepository.findByNickname("어린왕자");

    LetterEntity letter = letterRepository.save(
        LetterEntity.builder()
            .categoryName(utdto.getCategoryName())
            .birdName(mentor.getBird().getBirdName())
            .letterTitle(utdto.getTitle())
            .letter(utdto.getContent())
            .createAt(LocalDateTime.now())
            .build()
    );

    LetterStatusEntity letterStatusEntity = letterStatusRepository.save(
        LetterStatusEntity.builder()
            .mentee(mentee)
            .mentor(mentor)
            .menteeLetter(letter)
            .mentorLetter(null)
            .isMenteeRead(true)
            .isMentorRead(false)
            .isMenteeSaved(false)
            .isMentorSaved(false)
            .thanksType(null)
            .createAt(LocalDateTime.now())
            .build()
    );

    log.info("mentee : {} ", mentee.getNickname());
    log.info("mentor : {} ", mentor.getNickname());
    log.info("title : {} ", letter.getLetterTitle());
    log.info("content : {} ", letter.getLetter());
    log.info("letterStatus mentee: {} ", letterStatusEntity.getMentee().getNickname());
    log.info("letterStatus mentor: {} ", letterStatusEntity.getMentee().getNickname());
    log.info("letterStatus mentee Letter Title: {} ", letterStatusEntity.getMenteeLetter().getLetterTitle());
    log.info("letterStatus isMenteeRead: {} ", letterStatusEntity.isMenteeRead());
    log.info("letterStatus isMentorSaved: {} ", letterStatusEntity.isMentorSaved());
    log.info("letterStatus isMenteeSaved : {}", letterStatusEntity.isMenteeSaved());
    log.info("letterStatus isMentorSaved : {}", letterStatusEntity.isMentorSaved());
    log.info("letterStatus thanksType : {}", letterStatusEntity.getThanksType());

    return "편지 보내기 성공";
  }

}