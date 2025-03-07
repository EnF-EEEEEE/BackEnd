package com.enf.ut;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UTController {

  private final UTService utService;

  @PostMapping("/ut/test/send")
  public String sendLetterToMentor(@RequestBody UTDTO utdto) {

    return utService.sendLetterToMentor(utdto);
  }
}
