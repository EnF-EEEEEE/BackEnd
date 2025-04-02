package com.enf.domain.model.dto.response.letter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LetterHistoryDTO {

  private int sendLetter;

  private int replyLetter;

}
