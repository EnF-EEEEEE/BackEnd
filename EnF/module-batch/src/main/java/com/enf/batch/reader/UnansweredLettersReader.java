package com.enf.batch.reader;

import com.enf.domain.entity.LetterStatusEntity;
import com.enf.domain.repository.LetterStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnansweredLettersReader {

  private final LetterStatusRepository letterStatusRepository;

  public List<LetterStatusEntity> getUnansweredLetters() {

    return letterStatusRepository.findUnansweredLetters();
  }

}
