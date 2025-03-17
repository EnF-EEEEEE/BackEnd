package com.enf.service.impl;

import com.enf.component.facade.LetterFacade;
import com.enf.component.facade.UserFacade;
import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.letter.LetterDetailResponseDto;
import com.enf.model.dto.response.letter.LetterDetailsDTO;
import com.enf.model.dto.response.letter.LetterResponseDto;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.enf.model.dto.response.letter.ThrowLetterCategoryDTO;
import com.enf.model.type.LetterListType;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.LetterStatusRepository;
import com.enf.service.LetterService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LetterServiceImpl implements LetterService {

  private final UserFacade userFacade;
  private final LetterFacade letterFacade;
  private final RedisTemplate<String, Object> redisTemplate;
  private final LetterStatusRepository letterStatusRepository;
  private final MeterRegistry meterRegistry;

  /**
   * 사용자가 새로운 편지를 작성하여 상대방에게 전송하는 기능
   */

  @Override
  public ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter) {
    UserEntity mentee = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    UserEntity mentor = userFacade.getMentorByBirdAndCategory(sendLetter);

    LetterEntity letter = SendLetterDTO.of(sendLetter);
    LetterStatusEntity letterStatus = letterFacade.saveMenteeLetter(letter, mentee, mentor);

    userFacade.reduceQuota(mentee);
    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(letterStatus, mentor));

    // 메트릭 추가
    meterRegistry.counter("letter.sent").increment();
    return ResultResponse.of(SuccessResultType.SUCCESS_SEND_LETTER);
  }

  /**
   * 사용자가 받은 편지에 대해 답장을 작성하는 기능
   */
  @Override
  public ResultResponse replyLetter(HttpServletRequest request, ReplyLetterDTO replyLetter) {
    LetterStatusEntity letterStatus = letterFacade.saveMentorLetter(replyLetter);

    userFacade.reduceQuota(letterStatus.getMentor());
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
  public ResultResponse getArchiveLetterList(HttpServletRequest request, int pageNumber) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    PageResponse<ReceiveLetterDTO> letters = letterFacade.getLetterList(user, pageNumber, LetterListType.ARCHIVE);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_SAVE_LETTER, letters);
  }

  /**
   * 사용자가 특정 편지를 저장하는 기능
   */
  @Override
  public ResultResponse archiveLetter(HttpServletRequest request, Long letterStatusSeq) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    letterFacade.archiveLetter(user, letterStatusSeq);

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
    letterFacade.updateNotificationIsRead(letterStatus);

    return new ResultResponse(SuccessResultType.SUCCESS_GET_LETTER_DETAILS, letterDetails);
  }

  /**
   * 편지 전달(Throw) 로직을 수행하는 메서드
   */
  @Override
  public ResultResponse throwLetter(HttpServletRequest request, Long letterStatusSeq) {
    LetterStatusEntity letterStatus = letterFacade.getLetterStatus(letterStatusSeq);
    UserEntity newMentor = userFacade.getNewMentor(letterStatus);

    letterFacade.throwLetter(letterStatus);
    letterFacade.changeMentor(letterStatus, newMentor);

    redisTemplate.convertAndSend("notifications", NotificationDTO.sendLetter(letterStatus, newMentor));
    return ResultResponse.of(SuccessResultType.SUCCESS_THROW_LETTER);
  }

  /**
   * 고마움 전달 로직을 수행하는 메서드
   */
  @Override
  public ResultResponse thanksToMentor(HttpServletRequest request, Long letterSeq, String type) {
    LetterStatusEntity letterStatus = letterFacade.thanksToMentor(letterSeq, type);

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


  @Transactional(readOnly = true)
  public ResponseEntity<Map<String, Object>> getAllMenteeLetters(HttpServletRequest request, int page, int size) {

    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "menteeLetter.createAt"));
    Page<LetterStatusEntity> letterStatusPage = letterStatusRepository.findAllMenteeLetters(pageRequest);
    Page<LetterResponseDto> lettersPage = letterStatusPage.map(this::convertToDto);

    // null인 항목 필터링
    var filteredContent = lettersPage.getContent().stream()
            .filter(dto -> dto != null)
            .collect(Collectors.toList());

    Map<String, Object> response = new HashMap<>();
    response.put("letters", filteredContent);
    response.put("totalPages", lettersPage.getTotalPages());
    response.put("currentPage", lettersPage.getNumber());
    response.put("totalElements", lettersPage.getTotalElements());

    return ResponseEntity.ok(response);
  }

  private LetterResponseDto convertToDto(LetterStatusEntity letterStatus) {
    if (letterStatus.getMenteeLetter() == null) {
      log.warn("멘티 편지가 null인 데이터가 있습니다. ID: {}", letterStatus.getLetterStatusSeq());
      return null;
    }

    LetterEntity menteeLetter = letterStatus.getMenteeLetter();
    LetterEntity mentorLetter = letterStatus.getMentorLetter();

    // 답장 여부 확인
    boolean hasResponse = mentorLetter != null;
    LocalDateTime responseAt = hasResponse ? mentorLetter.getCreateAt() : null;

    // 저장 여부 확인 - NPE 방지를 위한 기본값 설정
    boolean isMenteeSaved = letterStatus.isMenteeSaved();

    return LetterResponseDto.builder()
            .id(letterStatus.getLetterStatusSeq())
            .title(menteeLetter.getLetterTitle())
            .content(menteeLetter.getLetter())
            .category(menteeLetter.getCategoryName())
            .sender(letterStatus.getMentee() != null ? letterStatus.getMentee().getNickname() : "알 수 없음")
            .receiver(letterStatus.getMentor() != null ? letterStatus.getMentor().getNickname() : "알 수 없음")
            .hasResponse(hasResponse)
            .isSaved(isMenteeSaved)
            .sentAt(menteeLetter.getCreateAt())
            .responseAt(responseAt)
            .build();
  }

  public ResponseEntity<LetterDetailResponseDto> getLetterContent(HttpServletRequest request, Long id) {


    LetterStatusEntity letterStatus = letterStatusRepository.findByLetterStatusSeq(id);
    LetterEntity menteeLetter = letterStatus.getMenteeLetter();
    LetterEntity mentorLetter = letterStatus.getMentorLetter();

    String mentorContent = "아직 답장이 작성되지 않았습니다.";
    LocalDateTime mentorResponseAt = null;
    if (mentorLetter!=null) {
      mentorContent = mentorLetter.getLetter();
      mentorResponseAt = mentorLetter.getCreateAt();
    }

    return ResponseEntity.ok(LetterDetailResponseDto.builder()
            .id(letterStatus.getLetterStatusSeq())
            .title(menteeLetter.getLetterTitle())
            .sender(letterStatus.getMentee().getNickname())
            .receiver(letterStatus.getMentor().getNickname())
            .category(menteeLetter.getCategoryName())
            .content(menteeLetter.getLetter())
            .sentAt(menteeLetter.getCreateAt())
            .hasResponse(letterStatus.getMentorLetter() != null)
            .responseContent(mentorContent)
            .responseAt(mentorResponseAt)
            .isSaved(letterStatus.isMenteeSaved())
            .build());
  }
}
