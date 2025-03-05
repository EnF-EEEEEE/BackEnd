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
import com.enf.model.dto.response.letter.ThrowLetterCategoryDTO;
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
   */
  @Override
  public ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter) {
    UserEntity mentee = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    UserEntity mentor = userFacade.getMentorByBirdAndCategory(sendLetter.getBirdName(), sendLetter.getCategoryName());

    LetterStatusEntity letterStatus = letterFacade.saveMenteeLetter(SendLetterDTO.of(sendLetter), mentee, mentor);
    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(letterStatus, mentor));

    return ResultResponse.of(SuccessResultType.SUCCESS_SEND_LETTER);
  }

  /**
   * 사용자가 받은 편지에 대해 답장을 작성하는 기능
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
   */
  @Override
  public ResultResponse getAllLetterList(HttpServletRequest request, int pageNumber) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    PageResponse<ReceiveLetterDTO> letters = letterFacade.getLetterList(user, pageNumber, LetterListType.ALL);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_ALL_LETTER, letters);
  }

  /**
   * 답장이 없는 미응답 편지 목록을 조회하는 기능 (페이징 지원)
   */
  @Override
  public ResultResponse getPendingLetterList(HttpServletRequest request, int pageNumber) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    PageResponse<ReceiveLetterDTO> letters = letterFacade.getLetterList(user, pageNumber, LetterListType.PENDING);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_PENDING_LETTER, letters);
  }

  /**
   * 사용자가 저장한 편지 목록을 조회하는 기능 (페이징 지원)
   */
  @Override
  public ResultResponse getSaveLetterList(HttpServletRequest request, int pageNumber) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    PageResponse<ReceiveLetterDTO> letters = letterFacade.getLetterList(user, pageNumber, LetterListType.SAVE);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_SAVE_LETTER, letters);
  }

  /**
   * 사용자가 특정 편지를 저장하는 기능
   */
  @Override
  public ResultResponse saveLetter(HttpServletRequest request, Long letterStatusSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    letterFacade.saveLetter(user, letterStatusSeq);

    return ResultResponse.of(SuccessResultType.SUCCESS_SAVE_LETTER);
  }

  /**
   * 특정 편지의 상세 정보를 조회하는 기능
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
   */
  @Override
  public ResultResponse throwLetter(HttpServletRequest request, Long letterStatusSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    if (user.getRole().getRoleName().equals("MENTEE")) {
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
   */
  @Override
  public ResultResponse thanksToMentor(HttpServletRequest request, Long letterSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    if (user.getRole().getRoleName().equals("MENTOR")) {
      throw new GlobalException(FailedResultType.MENTOR_PERMISSION_DENIED);
    }

    LetterStatusEntity letterStatus = letterFacade.thanksToMentor(letterSeq);
    redisTemplate.convertAndSend("notifications", NotificationDTO.thanksToMentor(letterStatus));

    return ResultResponse.of(SuccessResultType.SUCCESS_THANKS_TO_MENTOR);
  }

  /**
   * 카테고리별 편지 넘긴 개수 조회하는 기능
   */
  @Override
  public ResultResponse getThrowLetterCategory(HttpServletRequest request) {
    ThrowLetterCategoryDTO throwLetterCategory = ThrowLetterCategoryDTO.of(letterFacade.getThrowLetterCategory());

    return new ResultResponse(SuccessResultType.SUCCESS_GET_THROW_LETTER_CATEGORY, throwLetterCategory);
  }
}
