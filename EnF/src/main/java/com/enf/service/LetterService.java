package com.enf.service;

import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.letter.LetterDetailResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface LetterService {

  ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter);

  ResponseEntity<Map<String, Object>> getAllMenteeLetters(HttpServletRequest request, int page, int size);

  ResponseEntity<LetterDetailResponseDto> getLetterContent(HttpServletRequest request, Long id);
}
