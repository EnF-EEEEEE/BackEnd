package com.enf.service.impl;

import com.enf.component.facade.LetterFacade;
import com.enf.component.facade.UserFacade;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.letter.LetterDetailsDTO;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.LetterListType;
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
    UserEntity mentor = userFacade
        .getMentorByBirdAndCategory(sendLetter.getBirdName(), sendLetter.getCategoryName());

    LetterStatusEntity letterStatus = letterFacade.saveMenteeLetter(SendLetterDTO.of(sendLetter), mentee, mentor);
    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(letterStatus,
        mentor));

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
    LetterStatusEntity letterStatus = letterFacade.getLetterStatus(replyLetter.getLetterStatusSeq());

    letterFacade.saveMentorLetter(letterStatus, replyLetter);
    redisTemplate.convertAndSend("notifications", NotificationDTO.replyLetter(letterStatus));

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

    PageResponse<ReceiveLetterDTO> letters = letterFacade
        .getLetterList(user, pageNumber, LetterListType.ALL);

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
    PageResponse<ReceiveLetterDTO> letters = letterFacade
        .getLetterList(user, pageNumber, LetterListType.PENDING);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_PENDING_LETTER, letters);
  }

  /**
   * 사용자가 저장한 편지 목록을 조회하는 기능 (페이징 지원)
   * 1. 사용자 정보를 조회하여 본인의 저장된 편지 리스트를 가져온다.
   * 2. 저장한 편지 리스트 조회.
   * 3. 페이징 처리 후 결과 반환.
   *
   * @param request    HTTP 요청 객체 (토큰 확인)
   * @param pageNumber 요청한 페이지 번호
   * @return 저장된 편지 리스트 (페이지네이션 적용)
   */
  @Override
  public ResultResponse getSaveLetterList(HttpServletRequest request, int pageNumber) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    PageResponse<ReceiveLetterDTO> letters = letterFacade.getLetterList(user, pageNumber, LetterListType.SAVE);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_SAVE_LETTER, letters);
  }

  /**
   * 사용자가 특정 편지를 저장하는 기능
   * 1. 사용자 정보를 조회하여 본인을 식별
   * 2. 특정 편지를 저장 처리
   * 3. 저장 완료 후 성공 응답 반환
   *
   * @param request   HTTP 요청 객체 (토큰 확인)
   * @param letterStatusSeq 저장할 편지의 식별자
   * @return 편지 저장 결과 응답 객체
   */
  @Override
  public ResultResponse saveLetter(HttpServletRequest request, Long letterStatusSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    letterFacade.saveLetter(user, letterStatusSeq);

    return ResultResponse.of(SuccessResultType.SUCCESS_SAVE_LETTER);
  }

  /**
   * 특정 편지의 상세 정보를 조회하는 기능
   *
   * 1. 요청한 사용자의 정보를 토큰을 통해 조회한다.
   * 2. 조회된 사용자의 권한(멘티 또는 멘토)에 따라 편지 상세 정보를 가져온다.
   * 3. 편지의 상세 내용을 조회한다.
   * 4. 조회된 편지 상세 정보를 `ResultResponse` 객체로 감싸서 반환한다.
   *
   * @param request   HTTP 요청 객체 (토큰을 이용하여 사용자 인증)
   * @param letterStatusSeq 조회할 편지의 고유 식별자 (ID)
   * @return 편지 상세 정보를 포함한 응답 객체
   */
  @Override
  public ResultResponse getLetterDetails(HttpServletRequest request, Long letterStatusSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    LetterStatusEntity letterStatus = letterFacade.getLetterStatus(letterStatusSeq);
    LetterDetailsDTO letterDetails = letterFacade.getLetterDetails(user, letterStatus);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_LETTER_DETAILS, letterDetails);
  }

  /**
   * 편지 전달(Throw) 로직을 수행하는 메서드
   *
   * @param request          HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param letterStatusSeq  전달할 편지의 고유 식별자 (ID)
   * @return                 결과 응답 객체 (성공/실패 여부 포함)
   * @throws GlobalException 멘티가 편지를 전달하려 하거나, 이미 답장이 완료된 경우 예외 발생
   */
  @Override
  public ResultResponse throwLetter(HttpServletRequest request, Long letterStatusSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    if(user.getRole().getRoleName().equals("MENTEE")) {
      throw new GlobalException(FailedResultType.MENTEE_PERMISSION_DENIED);
    }

    LetterStatusEntity letterStatus = letterFacade.getLetterStatus(letterStatusSeq);
    letterFacade.throwLetter(letterStatus);
    UserEntity newMentor = userFacade.getNewMentor(letterStatus);

    letterFacade.updateMentor(letterStatus, newMentor);
    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(letterStatus, newMentor));

    return ResultResponse.of(SuccessResultType.SUCCESS_THROW_LETTER);
  }

  /**
   * 고마움 전달 로직을 수행하는 메서드
   *
   * @param request          HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param letterSeq        고마움을 전달할 멘토 편지의 고유 식별자 (ID)
   * @return                 결과 응답 객체 (성공/실패 여부 포함)
   * @throws GlobalException 멘토가 해당 경로를 호출할 경우 예외처리
   */
  @Override
  public ResultResponse thanksToMentor(HttpServletRequest request, Long letterSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    if(user.getRole().getRoleName().equals("MENTOR")) {
      throw new GlobalException(FailedResultType.MENTOR_PERMISSION_DENIED);
    }

    LetterStatusEntity letterStatus = letterFacade.thanksToMentor(letterSeq);
    redisTemplate.convertAndSend("notifications", NotificationDTO.thanksToMentor(letterStatus));

    return ResultResponse.of(SuccessResultType.SUCCESS_THANKS_TO_MENTOR);
  }
}