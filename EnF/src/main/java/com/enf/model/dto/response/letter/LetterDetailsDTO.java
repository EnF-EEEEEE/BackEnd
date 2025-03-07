package com.enf.model.dto.response.letter;

import com.enf.entity.LetterStatusEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LetterDetailsDTO {

  private LetterDTO replyLetter;

  private LetterDTO sendLetter;

  private boolean saved;

  public static LetterDetailsDTO ofMentee(LetterStatusEntity letterStatus) {
    LetterDTO replyLetter;
    LetterDTO sendLetter;

    if (letterStatus.getMentorLetter() == null) {
      sendLetter = LetterDTO.of(null, letterStatus.getMentee(), letterStatus.getMenteeLetter());
      return new LetterDetailsDTO(null, sendLetter, letterStatus.isMenteeSaved());
    }

    replyLetter = LetterDTO.of(
        letterStatus.getMentee(),
        letterStatus.getMentor(),
        letterStatus.getMentorLetter());

    sendLetter = LetterDTO.of(
        letterStatus.getMentor(),
        letterStatus.getMentee(),
        letterStatus.getMenteeLetter());

    return new LetterDetailsDTO(replyLetter, sendLetter, letterStatus.isMenteeSaved());
  }

  public static LetterDetailsDTO ofMentor(LetterStatusEntity letterStatus) {

    LetterDTO replyLetter = LetterDTO.of(
        letterStatus.getMentor(),
        letterStatus.getMentee(),
        letterStatus.getMenteeLetter()
    );

    if (letterStatus.getMentorLetter() == null) {
      return new LetterDetailsDTO(replyLetter, null, letterStatus.isMentorSaved());
    }

    LetterDTO sendLetter = LetterDTO.of(
        letterStatus.getMentee(),
        letterStatus.getMentor(),
        letterStatus.getMentorLetter()
    );

    return new LetterDetailsDTO(replyLetter, sendLetter, letterStatus.isMentorSaved());
  }
}