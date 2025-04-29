package com.enf.domain.model.dto.request.notification;

import com.enf.domain.entity.LetterStatusEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BatchNotificationDTO {

  private LetterStatusEntity letterStatus;

}
