package com.enf.api.service.impl;

import com.enf.api.component.facade.UserFacade;
import com.enf.api.exception.GlobalException;
import com.enf.domain.entity.BirdEntity;
import com.enf.domain.entity.TipsEntity;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.dto.response.bird.BirdExplanationDTO;
import com.enf.domain.model.dto.response.bird.BirdyListDTO;
import com.enf.domain.model.dto.response.bird.BirdyTipsDTO;
import com.enf.domain.model.type.FailedResultType;
import com.enf.domain.model.type.SuccessResultType;
import com.enf.domain.model.type.TokenType;
import com.enf.domain.repository.BirdRepository;
import com.enf.domain.repository.TipsRepository;
import com.enf.api.service.BirdyService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
    BirdEntity bird = birdRepository.findByBirdName(birdName)
        .orElseThrow(() -> new GlobalException(FailedResultType.BIRD_NOT_FOUND));

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

    String type;

    if (user.getRole().getRoleName().equals("ADMIN")) {
      type = "ALL";
    } else {
      List<String> types = List.of(user.getRole().getRoleName(), "ALL");
      type = types.get((int) (Math.random() * 2));
    }
    long birdSeq = (long) (Math.random() * 6) + 1;

    BirdEntity bird = birdRepository.findByBirdSeq(birdSeq);
    List<TipsEntity> tips = tipsRepository.findAllByType(type);

    BirdyTipsDTO birdyTips = BirdyTipsDTO.of(bird.getBirdName(), tips.get((int) (Math.random() * tips.size())));
    return new ResultResponse(SuccessResultType.SUCCESS_GET_BIRDY_TIPS, birdyTips);
  }
}
