package com.enf.domain.model.dto.request.inquiry;

import com.enf.domain.entity.InquiryEntity;
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
public class InquiryListDTO {
    private Long id;
    private String title;
    private String author;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private String status;

    public static InquiryListDTO from(InquiryEntity inquiry) {
        return InquiryListDTO.builder()
                .id(inquiry.getInquirySeq())
                .title(inquiry.getTitle())
                .author(inquiry.getUser().getNickname())
                .createdAt(inquiry.getCreateAt())
                .status(inquiry.getStatus().name())
                .build();
    }
}