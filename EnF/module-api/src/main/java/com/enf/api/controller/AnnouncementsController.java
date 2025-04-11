package com.enf.api.controller;

import com.enf.api.service.AnnouncementsService;
import com.enf.domain.model.dto.request.announcements.AnnouncementsDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("")
  public ResponseEntity<ResultResponse> getAnnouncements() {

    ResultResponse response = announcementsService.getAnnouncements();
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/details")
  public ResponseEntity<ResultResponse> getAnnouncementsDetails(
      @RequestParam(name = "announcementSeq") Long announcementSeq) {

    ResultResponse response = announcementsService.getAnnouncementsDetails(announcementSeq);
    return new ResponseEntity<>(response, response.getStatus());
  }

}
