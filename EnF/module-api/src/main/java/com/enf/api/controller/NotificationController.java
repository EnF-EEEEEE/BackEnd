package com.enf.api.controller;

import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.api.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

  private final NotificationService notificationService;

  /**
   * 실시간 알림을 위한 SSE 구독 API
   *
   * @param request HTTP 요청 객체
   * @return SseEmitter 객체 반환
   */
  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(HttpServletRequest request) {
    return notificationService.createEmitter(request);
  }

  @GetMapping("/list")
  public ResponseEntity<ResultResponse> notificationList(HttpServletRequest request) {

    ResultResponse response = notificationService.notificationList(request);
    return new ResponseEntity<>(response, response.getStatus());
  }

}
