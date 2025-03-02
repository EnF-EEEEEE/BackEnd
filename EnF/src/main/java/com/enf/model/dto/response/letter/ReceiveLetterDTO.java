package com.enf.model.dto.response.letter;

import java.time.LocalDateTime;
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

//  private boolean read;
//
//  private boolean saved;
}
