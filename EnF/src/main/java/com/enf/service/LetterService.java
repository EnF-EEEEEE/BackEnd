package com.enf.service;

import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.letter.LetterDetailResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface LetterService {

  ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter);

  ResponseEntity<Map<String, Object>> getAllMenteeLetters(HttpServletRequest request, int page, int size);

  ResponseEntity<LetterDetailResponseDto> getLetterContent(HttpServletRequest request, Long id);
  
  ResultResponse replyLetter(HttpServletRequest request, ReplyLetterDTO replyLetter);

  ResultResponse getAllLetterList(HttpServletRequest request, int pageNumber);

  ResultResponse getPendingLetterList(HttpServletRequest request, int pageNumber);

  ResultResponse getArchiveLetterList(HttpServletRequest request, int pageNumber);

  ResultResponse archiveLetter(HttpServletRequest request, Long letterStatusSeq);

  ResultResponse getLetterDetails(HttpServletRequest request, Long letterStatusSeq);

  ResultResponse throwLetter(HttpServletRequest request, Long letterStatusSeq);

  ResultResponse thanksToMentor(HttpServletRequest request, Long letterSeq, String type);

  ResultResponse getThrowLetterCategory(HttpServletRequest request);

  ResultResponse getLetterHistory(HttpServletRequest request);
}
