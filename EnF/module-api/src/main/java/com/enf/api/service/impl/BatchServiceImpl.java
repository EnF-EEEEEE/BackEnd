package com.enf.api.service.impl;

import com.enf.api.component.facade.LetterFacade;
import com.enf.api.component.facade.UserFacade;
import com.enf.api.service.BatchService;
import com.enf.domain.entity.LetterStatusEntity;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.model.dto.request.notification.NotificationDTO;
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
  public void sendNotificationToMentor(Long letterStatusSeq) {
    LetterStatusEntity letterStatus = letterFacade.getLetterStatus(letterStatusSeq);

    redisTemplate.convertAndSend("notifications", NotificationDTO.deadline(letterStatus));
  }

  @Override
  public void transferLetter(Long letterStatusSeq, Long transferSeq) {
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
  }

  @Override
  public void unlinkUser(Long userSeq) {
    UserEntity user = userFacade.findByUserSeq(userSeq);
    userFacade.withdrawal(user);
  }


}
