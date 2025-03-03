package com.enf.model.dto.response.letter;

import com.enf.entity.LetterStatusEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveLetterDTO {

  private Long letterSeq;

  private String birdName;

  private String nickname;

  private String title;

  private boolean read;

  private boolean saved;


  public static List<ReceiveLetterDTO> ofMentee(List<LetterStatusEntity> list) {
    return list.stream()
        .map(letterStatus -> {
          boolean isPending = letterStatus.getMentorLetter() == null;
          return new ReceiveLetterDTO(
              letterStatus.getLetterStatusSeq(),
              isPending ? "익명새" : letterStatus.getMentor().getBird().getBirdName(),
              isPending ? "익명새" : letterStatus.getMentor().getNickname(),
              isPending
                  ? letterStatus.getMenteeLetter().getLetterTitle()
                  : letterStatus.getMentorLetter().getLetterTitle(),
              letterStatus.isMenteeRead(),
              letterStatus.isMenteeSaved()
          );
        })
        .toList();
  }

  public static List<ReceiveLetterDTO> ofMentor(List<LetterStatusEntity> list) {
    return list.stream()
        .map(letterStatus ->
            new ReceiveLetterDTO(
                letterStatus.getLetterStatusSeq(),
                letterStatus.getMentee().getBird().getBirdName(),
                letterStatus.getMentee().getNickname(),
                letterStatus.getMenteeLetter().getLetterTitle(),
                letterStatus.isMentorRead(),
                letterStatus.isMentorSaved()
            )
        )
        .toList();
  }

}
