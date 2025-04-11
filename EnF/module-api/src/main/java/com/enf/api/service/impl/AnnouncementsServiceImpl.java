package com.enf.api.service.impl;

import com.enf.api.exception.GlobalException;
import com.enf.api.service.AnnouncementsService;
import com.enf.domain.entity.AnnouncementsEntity;
import com.enf.domain.model.dto.request.announcements.AnnouncementsDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.dto.response.announcements.Announcements;
import com.enf.domain.model.dto.response.announcements.AnnouncementsDetails;
import com.enf.domain.model.type.FailedResultType;
import com.enf.domain.model.type.SuccessResultType;
import com.enf.domain.repository.AnnouncementsRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

  @Override
  public ResultResponse getAnnouncements() {
    int currentDate = LocalDateTime.now().getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();

    // 이번 주 월요일 계산 (시작일)
    LocalDateTime startOfWeek = LocalDate.now().minusDays(currentDate).atTime(0, 0, 0);

    // 이번 주 일요일 계산 (종료일)
    LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

    List<Announcements> announcements = Announcements.of(announcementsRepository
        .findByCreatedAtBetween(startOfWeek, endOfWeek));


    return new ResultResponse(SuccessResultType.SUCCESS_GET_ANNOUNCEMENT, announcements);
  }

  @Override
  public ResultResponse getAnnouncementsDetails(Long announcementSeq) {
    AnnouncementsDetails announcementsDetails = AnnouncementsDetails.of(announcementsRepository
        .findById(announcementSeq)
        .orElseThrow(() -> new GlobalException(FailedResultType.ANNOUNCEMENT_NOT_FOUND)));

    return new ResultResponse(SuccessResultType.SUCCESS_CREATE_ANNOUNCEMENT, announcementsDetails);
  }


}
