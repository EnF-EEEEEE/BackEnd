package com.enf.api.service;

import com.enf.domain.model.dto.request.notification.AnnouncementsDTO;
import com.enf.domain.model.dto.response.ResultResponse;

public interface AnnouncementsService {

  ResultResponse createAnnouncements(AnnouncementsDTO announcementsDTO);
}
