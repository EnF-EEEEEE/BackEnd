package com.enf.service.impl;

import com.enf.component.facade.LetterFacade;
import com.enf.component.facade.UserFacade;
import com.enf.entity.LetterEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.service.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class LetterServiceImpl implements LetterService {

  private final UserFacade userFacade;
  private final LetterFacade letterFacade;
  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * 사용자가 새로운 편지를 작성하여 상대방에게 전송하는 기능
   * 1. 요청자의 정보를 조회하여 보낸 사용자(멘티) 식별
   * 2. 수신자의 정보를 조회 (멘토)
   * 3. 편지 정보를 저장 (멘티가 보낸 편지 저장)
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

    letterFacade.saveMenteeLetter(SendLetterDTO.of(sendLetter), mentee, mentor);
    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(mentee, mentor));

    return ResultResponse.of(SuccessResultType.SUCCESS_SEND_LETTER);
  }

  /**
   * 사용자가 받은 편지에 대해 답장을 작성하는 기능
   * 1. 요청자의 정보를 조회하여 보낸 사용자(멘토) 식별
   * 2. 수신자의 정보를 조회 (멘티)
   * 3. 원본 편지를 조회 (멘티가 보낸 편지)
   * 4. 답장 정보를 저장 (멘토 -> 멘티)
   * 5. Redis Pub/Sub을 이용해 알림 전송
   *
   * @param request     HTTP 요청 객체 (토큰 확인)
   * @param replyLetter 답장을 위해 사용자가 작성한 편지 정보
   * @return 답장 전송 결과 응답 객체
   */
  @Override
  public ResultResponse replyLetter(HttpServletRequest request, ReplyLetterDTO replyLetter) {
    UserEntity mentor = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    UserEntity mentee = userFacade.findByNickname(replyLetter.getReceiveUser());

    LetterEntity menteeLetter = letterFacade.findLetterByLetterSeq(replyLetter.getLetterSeq());

    letterFacade.saveMentorLetter(ReplyLetterDTO.of(replyLetter), menteeLetter);
    redisTemplate.convertAndSend("notifications", NotificationDTO.replyLetter(mentor, mentee));

    return ResultResponse.of(SuccessResultType.SUCCESS_RECEIVE_LETTER);
  }

  /**
   * 사용자가 받은 모든 편지를 조회하는 기능 (페이징 지원)
   *
   * @param request    HTTP 요청 객체 (토큰 확인)
   * @param pageNumber 요청한 페이지 번호
   * @return 받은 편지 리스트 (페이지네이션 적용)
   */
  @Override
  public ResultResponse getAllLetterList(HttpServletRequest request, int pageNumber) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    PageResponse<ReceiveLetterDTO> letters = letterFacade.getAllLetterList(user, pageNumber);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_ALL_LETTER, letters);
  }

  /**
   * 답장이 없는 미응답 편지 목록을 조회하는 기능 (페이징 지원)
   * 1. 요청자의 정보를 조회하여 사용자 식별
   * 2. 미응답 편지 리스트 조회
   * 3. 페이징 처리 후 결과 반환
   *
   * @param request    HTTP 요청 객체 (토큰 확인)
   * @param pageNumber 요청한 페이지 번호
   * @return 미응답 편지 리스트 (페이지네이션 적용)
   */
  @Override
  public ResultResponse getPendingLetterList(HttpServletRequest request, int pageNumber) {
    // 사용자 정보 조회
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    // 미응답 편지 리스트 조회 및 페이징 처리
    PageResponse<ReceiveLetterDTO> letters = letterFacade.getPendingLetterList(user, pageNumber);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_PENDING_LETTER, letters);
  }
}