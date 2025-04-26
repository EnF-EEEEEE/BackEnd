package com.enf.api.controller;

import com.enf.api.service.BatchService;
import com.enf.domain.model.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batch")
public class BatchController {

  private final BatchService batchService;

  @GetMapping("/deadline/notification")
  public ResponseEntity<?> sendNotification(
      @RequestParam(name = "letterStatusSeq") Long letterStatusSeq) {

    batchService.sendNotificationToMentor(letterStatusSeq);
    return ResponseEntity.ok("Batch 작업 : 알림 전송 성공");
  }

  @GetMapping("/transfer/letter")
  public ResponseEntity<?> transferLetter(
      @RequestParam(name = "letterStatusSeq") Long letterStatusSeq,
      @RequestParam(name = "transferSeq") Long transferSeq) {

    batchService.transferLetter(letterStatusSeq, transferSeq);
    return ResponseEntity.ok("Batch 작업 : 편지 넘기기 성공");
  }

  @GetMapping("/unlink/user")
  public ResponseEntity<?> unlinkUser(@RequestParam(name = "userSeq") Long userSeq) {

    batchService.unlinkUser(userSeq);
    return ResponseEntity.ok("Batch 작업 : 회원 탈퇴 성공 전송 성공");
  }

}
