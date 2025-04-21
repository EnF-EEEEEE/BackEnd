package com.enf.batch.writer;

import com.enf.domain.entity.LetterStatusEntity;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.repository.LetterStatusRepository;
import com.enf.domain.repository.UserRepository;
import com.enf.domain.repository.querydsl.UserQueryRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnansweredLettersWriter implements ItemWriter<LetterStatusEntity> {

  private final LetterStatusRepository letterStatusRepository;
  private final UserQueryRepository userQueryRepository;
  private final UserRepository userRepository;

  @Override
  public void write(Chunk<? extends LetterStatusEntity> chunk) throws Exception {
    if (chunk.getItems().isEmpty()) {
      log.info("답장을 하지 않은 멘토가 없습니다.");
      return;
    }

    for(LetterStatusEntity letterStatus : chunk) {

      if (letterStatus.getMentor().getRole().getRoleName().equals("ADMIN")) {
        return;
      }

      LocalDate letterDate = letterStatus.getCreateAt().toLocalDate();
      LocalDate currentDate = LocalDate.now();

      // 현재 날짜와의 차이 계산
      int difference = (int) ChronoUnit.DAYS.between(letterDate, currentDate);

      switch (difference) {
        // 편지를 처음 받은 멘토의 답장 기간이 1일 남았을 경우
        case 3:
          break;
        // 편지를 처음 받은 멘토의 답장 기간이 지났을 경우
        case 4:
          sendLetterToNewMentor(letterStatus, difference);
          break;
        // 편지를 두 번째로 받은 멘토의 답장 기간이 1일 남았을 경우
        case 6:
          break;
        // 편지를 두 번째로 받은 멘토의 답장 기간이 1일 남았을 경우
        case 7:
          sendLetterToNewMentor(letterStatus, difference);
          break;
      }
    }
  }

  private void sendLetterToNewMentor(LetterStatusEntity letterStatus, int difference) {
    if (difference == 4) {
      String birdName = letterStatus.getMenteeLetter().getBirdName();
      String categoryName = letterStatus.getMenteeLetter().getCategoryName();
      Long letterStatusSeq = letterStatus.getLetterStatusSeq();

      UserEntity newMentor = userQueryRepository.getMentor(birdName, categoryName, letterStatusSeq);
      letterStatusRepository.changeMentor(letterStatus.getLetterStatusSeq(), newMentor);
      return;
    }

    UserEntity admin = userRepository.findByNickname("지미니짱짱");
    letterStatusRepository.changeMentor(letterStatus.getLetterStatusSeq(), admin);
  }


}
