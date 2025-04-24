package com.enf.api.service;

import com.enf.domain.model.dto.response.ResultResponse;

public interface BatchService {

  ResultResponse sendNotificationToMentor(Long letterStatusSeq);

  ResultResponse transferLetter(Long letterStatusSeq, Long transferSeq);
}
