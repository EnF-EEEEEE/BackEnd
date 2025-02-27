package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.entity.LetterEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.LetterRepository;
import com.enf.repository.querydsl.UserQueryRepository;
import com.enf.service.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LetterServiceImpl implements LetterService {

  private final UserFacade userFacade;
  private final UserQueryRepository userQueryRepository;
  private final LetterRepository letterRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter) {
    UserEntity sendUser = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity receiveUser = userQueryRepository
        .getSendUser(sendLetter.getBirdName(), sendLetter.getCategoryName());

    LetterEntity letter = LetterEntity.builder()
        .sendUser(sendUser)
        .receiveUser(receiveUser)
        .categoryName(sendLetter.getCategoryName())
        .letterTitle(sendLetter.getTitle())
        .letter(sendLetter.getLetter())
        .createAt(LocalDateTime.now())
        .build();

    letterRepository.save(letter);

    redisTemplate.convertAndSend(
        "notifications",
        new NotificationDTO(receiveUser.getUserSeq(), "편지가 도착했어요"));

    return ResultResponse.of(SuccessResultType.SUCCESS_SEND_LETTER);
  }
}
