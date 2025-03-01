package com.enf.controller;

import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.service.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/letter")
public class LetterController {

  private final LetterService letterService;

  /**
   * 편지 전송 API
   *
   * @param request    HTTP 요청 객체
   * @param sendLetter 편지 전송 요청 DTO
   * @return 전송 결과 응답
   */
  @PostMapping("/send")
  public ResponseEntity<ResultResponse> sendLetter(
      HttpServletRequest request,
      @RequestBody SendLetterDTO sendLetter) {

    ResultResponse response = letterService.sendLetter(request, sendLetter);
    return new ResponseEntity<>(response, response.getStatus());
  }

  @PostMapping("/reply")
  public ResponseEntity<ResultResponse> replyLetter(
      HttpServletRequest request,
      @RequestBody ReplyLetterDTO reply) {

    ResultResponse response = letterService.replyLetter(request, reply);
    return new ResponseEntity<>(response, response.getStatus());
  }
}