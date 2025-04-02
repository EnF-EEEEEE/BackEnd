package com.enf.domain.model.dto.response.user;

import com.enf.domain.entity.UserEntity;
import com.enf.domain.model.dto.request.user.UserCategoryDTO;
import com.enf.domain.model.dto.response.letter.LetterHistoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

  private String birdName;

  private String nickname;

  private String roleName;

  private UserCategoryDTO userCategory;

  private int quota;

  private boolean isRead;

  private int sendLetter;

  private int replyLetter;

  public static UserProfileDTO of(UserEntity user, boolean isRead, LetterHistoryDTO letterHistory) {
    return new UserProfileDTO(
        user.getBird().getBirdName(),
        user.getNickname(),
        user.getRole().getRoleName(),
        user.getRole().getRoleName().equals("MENTOR")
            ? UserCategoryDTO.of(user.getCategory())
            : null,
        user.getQuota(),
        isRead,
        letterHistory.getSendLetter(),
        letterHistory.getReplyLetter()
    );

  }
}
