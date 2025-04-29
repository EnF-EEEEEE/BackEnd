package com.enf.domain.model.dto.response.letter;

import com.enf.domain.entity.LetterStatusEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnansweredLetterDTO {

  private String type;

  private List<LetterStatusEntity> letterStatusList;

}
