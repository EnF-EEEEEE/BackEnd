package com.enf.api.controller;

import com.enf.api.service.AnnouncementsService;
import com.enf.domain.model.dto.request.notification.AnnouncementsDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/announcements")
public class AnnouncementsController {

  private final AnnouncementsService announcementsService;

  @PostMapping("")
  public ResponseEntity<ResultResponse> createAnnouncements(AnnouncementsDTO announcementsDTO) {

    ResultResponse response = announcementsService.createAnnouncements(announcementsDTO);
    return new ResponseEntity<>(response, response.getStatus());
  }

}
