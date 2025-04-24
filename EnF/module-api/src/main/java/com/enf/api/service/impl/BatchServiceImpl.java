package com.enf.api.service.impl;

import com.enf.api.component.facade.LetterFacade;
import com.enf.api.component.facade.UserFacade;
import com.enf.api.service.BatchService;
import com.enf.domain.entity.LetterStatusEntity;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.model.dto.request.notification.NotificationDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.type.SuccessResultType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

  private final LetterFacade letterFacade;
  private final UserFacade userFacade;
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public ResultResponse sendNotificationToMentor(Long letterStatusSeq) {
    LetterStatusEntity letterStatus = letterFacade.getLetterStatus(letterStatusSeq);

    redisTemplate.convertAndSend("notifications", NotificationDTO.deadline(letterStatus));
    return ResultResponse.of(SuccessResultType.SUCCESS_BATCH_NOTIFICATION_JOB);
  }

  @Override
  public ResultResponse transferLetter(Long letterStatusSeq, Long transferSeq) {
    LetterStatusEntity letterStatus = letterFacade.getLetterStatus(letterStatusSeq);
    letterFacade.throwLetter(letterStatus);

    UserEntity newMentor;
    if (transferSeq == 1) {
      newMentor = userFacade.getNewMentor(letterStatus);
    } else {
      newMentor = userFacade.findByUserSeq(5L);
    }

    letterFacade.changeMentor(letterStatus, newMentor);

    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(letterStatus, newMentor));
    return ResultResponse.of(SuccessResultType.SUCCESS_TRANSFER_LETTER);
  }


}
