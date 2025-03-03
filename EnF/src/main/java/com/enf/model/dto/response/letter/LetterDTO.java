package com.enf.model.dto.response.letter;

import com.enf.entity.LetterEntity;
import com.enf.entity.UserEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LetterDTO {

  private Long letterSeq;

  private String replyUserBird;

  private String replyUser;

  private String letterTitle;

  private String categoryName;

  private String letter;

  private LocalDateTime creatAt;

  private String sendUserBird;

  private String sendUser;

  public static LetterDTO of(UserEntity replyUser, UserEntity sendUser, LetterEntity letter) {

    return new LetterDTO(
        letter.getLetterSeq(),
        replyUser == null ? "익명새" : replyUser.getBird().getBirdName(),
        replyUser == null ? "익명새" : replyUser.getNickname(),
        letter.getLetterTitle(),
        letter.getCategoryName(),
        letter.getLetter(),
        letter.getCreateAt(),
        sendUser.getBird().getBirdName(),
        sendUser.getNickname()
    );
  }
}
