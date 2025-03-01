package com.enf.service.impl;

import com.enf.component.facade.LetterFacade;
import com.enf.component.facade.UserFacade;
import com.enf.entity.LetterEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.service.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 편지 서비스 구현체
 * 사용자가 편지를 작성하고 상대방에게 전송하는 기능을 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LetterServiceImpl implements LetterService {

  private final UserFacade userFacade;
  private final LetterFacade letterFacade;
  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * 사용자가 편지를 작성하고 상대방에게 전송하는 기능
   * 1. 요청자의 정보를 조회하여 보낸 사용자 식별
   * 2. 수신자의 정보를 조회
   * 3. 편지 정보를 저장
   * 4. Redis Pub/Sub을 이용해 알림 전송
   *
   * @param request    HTTP 요청 객체 (토큰 확인)
   * @param sendLetter 사용자가 작성한 편지 정보
   * @return 편지 전송 결과 응답 객체
   */
  @Override
  public ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter) {
    UserEntity mentee = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    UserEntity mentor = userFacade.getMentorByBirdAndCategory(sendLetter);

    letterFacade.saveLetter(SendLetterDTO.of(mentee, mentor, sendLetter));
    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(mentee, mentor));

    return ResultResponse.of(SuccessResultType.SUCCESS_SEND_LETTER);
  }


  /**
   * 사용자가 편지를 확인하고 상대방에게 답장하는 기능
   * 1. 요청자의 정보를 조회하여 보낸 사용자 식별
   * 2. 수신자의 정보를 조회
   * 3. 편지 정보를 저장
   * 4. Redis Pub/Sub을 이용해 알림 전송
   *
   * @param request    HTTP 요청 객체 (토큰 확인)
   * @param replyLetter 답장을 위해 사용자가 작성한 편지 정보
   * @return 편지 전송 결과 응답 객체
   */
  @Override
  public ResultResponse replyLetter(HttpServletRequest request, ReplyLetterDTO replyLetter) {
    UserEntity mentor = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    UserEntity mentee = userFacade.findByNickname(replyLetter.getReceiveUser());

    LetterEntity menteeLetter = letterFacade.findLetterByLetterSeq(replyLetter.getLetterSeq());

    letterFacade.saveLetter(ReplyLetterDTO.of(mentor, mentee, replyLetter, menteeLetter));
    redisTemplate.convertAndSend("notifications", NotificationDTO.replyLetter(mentor, mentee));

    return ResultResponse.of(SuccessResultType.SUCCESS_RECEIVE_LETTER);
  }
}