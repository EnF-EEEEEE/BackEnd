package com.enf.controller;

import com.enf.model.dto.response.ResultResponse;
import com.enf.service.BirdyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/birdy")
public class BirdyController {

  private final BirdyService birdyService;

  @GetMapping("/test/birdy")
  public ResponseEntity<ResultResponse> getTestBirdy(@RequestParam("birdName") String birdName) {

    ResultResponse response = birdyService.getTestBirdy(birdName);
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/letter/birdy")
  public ResponseEntity<ResultResponse> getLetterBirdy() {

    ResultResponse response = birdyService.getLetterBirdy();
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/myPage/birdy")
  public ResponseEntity<ResultResponse> getMyPageBirdy() {

    ResultResponse response = birdyService.getMyPageBirdy();
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/tip")
  public ResponseEntity<ResultResponse> getBirdyTip(HttpServletRequest request) {

    ResultResponse response = birdyService.getBirdyTip(request);
    return new ResponseEntity<>(response, response.getStatus());
  }
}
