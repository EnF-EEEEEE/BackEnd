package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.letter.LetterDetailResponseDto;
import com.enf.model.dto.response.letter.LetterResponseDto;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.LetterRepository;
import com.enf.repository.LetterStatusRepository;
import com.enf.repository.querydsl.UserQueryRepository;
import com.enf.service.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LetterServiceImpl implements LetterService {

  private final UserFacade userFacade;
  private final UserQueryRepository userQueryRepository;
  private final LetterRepository letterRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  private final LetterStatusRepository letterStatusRepository;

  @Override
  public ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter) {
    UserEntity sendUser = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity receiveUser = userQueryRepository
        .getSendUser(sendLetter.getBirdName(), sendLetter.getCategoryName());

    LetterEntity letter = LetterEntity.builder()
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
