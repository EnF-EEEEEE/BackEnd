package com.enf.domain.model.dto.response.bird;

import com.enf.domain.entity.BirdEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BirdExplanationDTO {

  private String birdName;

  private String traits;

  private String explanation;

  public static BirdExplanationDTO toTestBirdy(BirdEntity bird) {
    return new BirdExplanationDTO(bird.getBirdName(), bird.getTraits(), bird.getTestBirdy());
  }

  public static BirdExplanationDTO toLetterBirdy(BirdEntity bird) {
    return new BirdExplanationDTO(bird.getBirdName(), bird.getTraits(), bird.getLetterBirdy());
  }

  public static BirdExplanationDTO toMyPageBirdy(BirdEntity bird) {
    return new BirdExplanationDTO(bird.getBirdName(), bird.getTraits(), bird.getMyPageBirdy());
  }
}
