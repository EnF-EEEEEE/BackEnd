package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.entity.BirdEntity;
import com.enf.entity.TipsEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.bird.BirdExplanationDTO;
import com.enf.model.dto.response.bird.BirdyListDTO;
import com.enf.model.dto.response.bird.BirdyTips;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.BirdRepository;
import com.enf.repository.TipsRepository;
import com.enf.service.BirdyService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BirdyServiceImpl implements BirdyService {

  private final UserFacade userFacade;
  private final BirdRepository birdRepository;
  private final TipsRepository tipsRepository;

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
  public ResultResponse getMyPageBirdy() {
    List<BirdEntity> birdList = birdRepository.findAll();

    BirdyListDTO birdyList = BirdyListDTO.toMyPageBirdyList(birdList);
    return new ResultResponse(SuccessResultType.SUCCESS_GET_ALL_BIRDY, birdyList);
  }

  @Override
  public ResultResponse getBirdyTip(HttpServletRequest request) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    List<String> types = List.of(user.getRole().getRoleName(), "ALL");

    String type = types.get((int) (Math.random() * 2));
    long birdSeq = (long) (Math.random() * 6) + 1;

    BirdEntity bird = birdRepository.findByBirdSeq(birdSeq);
    List<TipsEntity> tips = tipsRepository.findAllByType(type);

    BirdyTips birdyTips = BirdyTips.of(bird.getBirdName(), tips.get((int) (Math.random() * tips.size())));
    return new ResultResponse(SuccessResultType.SUCCESS_GET_BIRDY_TIPS, birdyTips);
  }
}
