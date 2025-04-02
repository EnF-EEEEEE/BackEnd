package com.enf.domain.model.dto.response.bird;

import com.enf.domain.entity.BirdEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BirdyListDTO {

  private List<BirdExplanationDTO> birdyList;

  public static BirdyListDTO toLetterBirdyList(List<BirdEntity> birdList) {
    List<BirdExplanationDTO> birdyList = new ArrayList<>();

    for (BirdEntity bird : birdList) {
      birdyList.add(BirdExplanationDTO.toLetterBirdy(bird));
    }

    return new BirdyListDTO(birdyList);
  }

  public static BirdyListDTO toMyPageBirdyList(List<BirdEntity> birdList) {
    List<BirdExplanationDTO> birdyList = new ArrayList<>();

    for (BirdEntity bird : birdList) {
      birdyList.add(BirdExplanationDTO.toMyPageBirdy(bird));
    }

    return new BirdyListDTO(birdyList);
  }
}
