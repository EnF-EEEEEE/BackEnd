package com.enf.controller;

import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.service.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/letter")
public class LetterController {

  private final LetterService letterService;

  /**
   * 편지 전송 API
   *
   * @param request    HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param sendLetter 편지 전송 요청 DTO
   * @return 전송 결과 응답 (성공/실패 여부 포함)
   */
  @PostMapping("/send")
  public ResponseEntity<ResultResponse> sendLetter(
      HttpServletRequest request,
      @RequestBody SendLetterDTO sendLetter) {

    ResultResponse response = letterService.sendLetter(request, sendLetter);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 편지 답장 API
   *
   * @param request HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param reply   답장 요청 DTO
   * @return 답장 결과 응답 (성공/실패 여부 포함)
   */
  @PostMapping("/reply")
  public ResponseEntity<ResultResponse> replyLetter(
      HttpServletRequest request,
      @RequestBody ReplyLetterDTO reply) {

    ResultResponse response = letterService.replyLetter(request, reply);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 편지 목록 조회 API
   *
   * @param request    HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param pageNumber 조회할 페이지 번호
   * @return 편지 목록 응답 (페이징된 데이터 포함)
   */
  @GetMapping("/list/all")
  public ResponseEntity<ResultResponse> getAllLetterList(HttpServletRequest request,
      @RequestParam(name = "pageNumber") int pageNumber) {

    ResultResponse response = letterService.getAllLetterList(request, pageNumber);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 답장이 없는 미응답 편지 목록 조회 API
   *
   * @param request    HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param pageNumber 조회할 페이지 번호
   * @return 미응답 편지 목록 응답 (페이징된 데이터 포함)
   */
  @GetMapping("/list/pending")
  public ResponseEntity<ResultResponse> getPendingLetterList(HttpServletRequest request,
      @RequestParam(name = "pageNumber") int pageNumber) {

    ResultResponse response = letterService.getPendingLetterList(request, pageNumber);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 저장된 편지 목록 조회 API
   *
   * @param request    HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param pageNumber 조회할 페이지 번호
   * @return 저장된 편지 목록 응답 (페이징된 데이터 포함)
   */
  @GetMapping("/list/save")
  public ResponseEntity<ResultResponse> getSaveLetterList(HttpServletRequest request,
      @RequestParam(name = "pageNumber") int pageNumber) {

    ResultResponse response = letterService.getSaveLetterList(request, pageNumber);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 특정 편지를 저장하는 API (멘티, 멘토 공통)
   *
   * @param request   HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param letterStatusSeq 저장할 편지의 고유 식별자 (ID)
   * @return 저장 결과 응답 (성공/실패 여부 포함)
   */
  @GetMapping("/save")
  public ResponseEntity<ResultResponse> saveLetter(HttpServletRequest request,
      @RequestParam(name = "letterStatusSeq") Long letterStatusSeq) {

    ResultResponse response = letterService.saveLetter(request, letterStatusSeq);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 편지 상세 조회 API
   *
   * @param request   HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param letterStatusSeq 조회할 편지의 고유 식별자 (ID)
   * @return 편지 상세 정보 응답 (성공/실패 여부 포함)
   */
  @GetMapping("/details")
  public ResponseEntity<ResultResponse> getLetterDetails(HttpServletRequest request,
      @RequestParam(name = "letterStatusSeq") Long letterStatusSeq) {

    ResultResponse response = letterService.getLetterDetails(request, letterStatusSeq);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 편지 전달(Throw) API
   *
   * @param request           HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param letterStatusSeq   전달할 편지의 고유 식별자 (ID)
   * @return                  결과 응답 객체 (성공/실패 여부 포함)
   */
  @GetMapping("/throw")
  public ResponseEntity<ResultResponse> throwLetter(HttpServletRequest request,
      @RequestParam(name = "letterStatusSeq") Long letterStatusSeq) {

    ResultResponse response = letterService.throwLetter(request, letterStatusSeq);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 편지 전달(Throw) API
   *
   * @param request           HTTP 요청 객체 (사용자 인증 정보 포함)
   * @param letterSeq         고마움 표시할 멘토 편지의 고유 식별자 (ID)
   * @return                  결과 응답 객체 (성공/실패 여부 포함)
   */
  @GetMapping("/thanks")
  public ResponseEntity<ResultResponse> thanksToMentor(HttpServletRequest request,
      @RequestParam(name = "letterSeq") Long letterSeq) {

    ResultResponse response = letterService.thanksToMentor(request, letterSeq);
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/throws/category")
  public ResponseEntity<ResultResponse> getThrowLetterCategory(HttpServletRequest request) {

    ResultResponse response = letterService.getThrowLetterCategory(request);
    return new ResponseEntity<>(response, response.getStatus());
  }
}
