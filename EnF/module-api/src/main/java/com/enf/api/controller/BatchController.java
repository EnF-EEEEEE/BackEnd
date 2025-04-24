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
  public ResponseEntity<ResultResponse> sendNotification(
      @RequestParam(name = "letterStatusSeq") Long letterStatusSeq) {

    ResultResponse response = batchService.sendNotificationToMentor(letterStatusSeq);
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/transfer/letter")
  public ResponseEntity<ResultResponse> transferLetter(
      @RequestParam(name = "letterStatusSeq") Long letterStatusSeq,
      @RequestParam(name = "transferSeq") Long transferSeq) {

    ResultResponse response = batchService.transferLetter(letterStatusSeq, transferSeq);
    return new ResponseEntity<>(response, response.getStatus());
  }

}
