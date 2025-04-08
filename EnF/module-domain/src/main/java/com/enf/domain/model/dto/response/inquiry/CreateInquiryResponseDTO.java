package com.enf.domain.model.dto.response.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInquiryResponseDTO {
    private String message;
    private Long inquiryId;

    public static CreateInquiryResponseDTO of(Long inquiryId) {
        return CreateInquiryResponseDTO.builder()
                .message("문의가 성공적으로 등록되었습니다.")
                .inquiryId(inquiryId)
                .build();
    }
}
