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
public class InquiryDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String author;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private String status;
    private InquiryResponseDTO response;

    public static InquiryDetailDTO from(InquiryEntity inquiry) {
        InquiryDetailDTOBuilder builder = InquiryDetailDTO.builder()
                .id(inquiry.getInquirySeq())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .author(inquiry.getUser().getNickname())
                .createdAt(inquiry.getCreateAt())
                .status(inquiry.getStatus().name());

        // 답변이 있는 경우 응답에 포함
        if (inquiry.getStatus() == InquiryEntity.InquiryStatus.ANSWERED && inquiry.getResponse() != null) {
            builder.response(InquiryResponseDTO.from(inquiry.getResponse()));
        }

        return builder.build();
    }
}