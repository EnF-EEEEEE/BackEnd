package com.enf.api.service;

import com.enf.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface BirdyService {

  ResultResponse getTestBirdy(String birdName);

  ResultResponse getLetterBirdy();

  ResultResponse getMyPageBirdy();

  ResultResponse getBirdyTip(HttpServletRequest request);
}
