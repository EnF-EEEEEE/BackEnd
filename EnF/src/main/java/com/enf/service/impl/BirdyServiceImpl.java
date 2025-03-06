package com.enf.service.impl;

import com.enf.entity.BirdEntity;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.bird.BirdExplanationDTO;
import com.enf.model.dto.response.bird.BirdyListDTO;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.BirdRepository;
import com.enf.service.BirdyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BirdyServiceImpl implements BirdyService {

  private final BirdRepository birdRepository;

  @Override
  public ResultResponse getTestBirdy(String birdName) {
    BirdEntity bird = birdRepository.findByBirdName(birdName);

    BirdExplanationDTO birdExplanation = BirdExplanationDTO.toTestBirdy(bird);
    return new ResultResponse(SuccessResultType.SUCCESS_GET_TEST_RESULT_BIRDY, birdExplanation);
  }

  @Override
  public ResultResponse getLetterBirdy() {
    List<BirdEntity> birdList = birdRepository.findAll();

    BirdyListDTO birdyList = BirdyListDTO.toLetterBirdyList(birdList);
    return new ResultResponse(SuccessResultType.SUCCESS_GET_LETTER_BIRDY, birdyList);
  }

  @Override
  public ResultResponse getAllBirdy() {
    List<BirdEntity> birdList = birdRepository.findAll();

    BirdyListDTO birdyList = BirdyListDTO.toAllBirdyList(birdList);
    return new ResultResponse(SuccessResultType.SUCCESS_GET_ALL_BIRDY, birdyList);
  }
}
