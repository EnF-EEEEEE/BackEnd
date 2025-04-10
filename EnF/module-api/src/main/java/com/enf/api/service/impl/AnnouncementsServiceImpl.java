package com.enf.api.service.impl;

import com.enf.api.service.AnnouncementsService;
import com.enf.domain.entity.AnnouncementsEntity;
import com.enf.domain.model.dto.request.notification.AnnouncementsDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.type.SuccessResultType;
import com.enf.domain.repository.AnnouncementsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnouncementsServiceImpl implements AnnouncementsService {

  private final AnnouncementsRepository announcementsRepository;


  @Override
  public ResultResponse createAnnouncements(AnnouncementsDTO announcementsDTO) {
    AnnouncementsEntity announcements = AnnouncementsDTO.of(announcementsDTO);
    announcementsRepository.save(announcements);

    return ResultResponse.of(SuccessResultType.SUCCESS_CREATE_ANNOUNCEMENT);
  }
}
