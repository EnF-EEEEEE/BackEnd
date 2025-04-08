package com.enf.domain.model.dto.request.inquiry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class InquiryDTO {

    @JsonProperty("content")
    private String content;

    @JsonCreator
    public InquiryDTO(String content) {
        this.content = content;
    }
}
