package com.enf.api.service;

import com.enf.domain.model.dto.request.announcements.AnnouncementsDTO;
import com.enf.domain.model.dto.response.ResultResponse;

public interface AnnouncementsService {

  ResultResponse createAnnouncements(AnnouncementsDTO announcementsDTO);

  ResultResponse getAnnouncements();

  ResultResponse getAnnouncementsDetails(Long announcementSeq);
}
