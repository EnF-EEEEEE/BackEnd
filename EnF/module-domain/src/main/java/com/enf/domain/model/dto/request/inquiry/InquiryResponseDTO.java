package com.enf.domain.model.dto.request.inquiry;

import com.enf.domain.entity.InquiryResponseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDTO {
    private Long id;
    private String content;
    private String respondent;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static InquiryResponseDTO from(InquiryResponseEntity response) {
        return InquiryResponseDTO.builder()
                .id(response.getResponseSeq())
                .content(response.getContent())
                .respondent(response.getAdmin().getNickname())
                .createdAt(response.getCreateAt())
                .build();
    }
}