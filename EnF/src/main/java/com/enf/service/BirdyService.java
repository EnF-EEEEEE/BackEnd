package com.enf.service;

import com.enf.model.dto.response.ResultResponse;

public interface BirdyService {

  ResultResponse getTestBirdy(String birdName);

  ResultResponse getLetterBirdy();

  ResultResponse getAllBirdy();
}
