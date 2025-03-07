package com.enf.model.dto.response.bird;

import com.enf.entity.TipsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BirdyTipsDTO {

  private String birdName;

  private String tip;

  public static BirdyTipsDTO of(String birdName, TipsEntity tip) {
    return new BirdyTipsDTO(birdName, tip.getTip());
  }

}
