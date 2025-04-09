package com.enf.api.component.badword;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BadWordExceptionDTO {

    private String fieldName;
    private String badWord;
}
