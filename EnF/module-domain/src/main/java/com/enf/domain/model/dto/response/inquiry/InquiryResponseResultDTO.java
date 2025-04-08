package com.enf.domain.model.dto.response.inquiry;

import com.enf.domain.entity.InquiryResponseEntity;
import com.enf.domain.model.dto.request.inquiry.InquiryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseResultDTO {
    private String message;
    private InquiryResponseDTO response;

    public static InquiryResponseResultDTO from(InquiryResponseEntity responseEntity) {
        return InquiryResponseResultDTO.builder()
                .message("답변이 성공적으로 등록되었습니다.")
                .response(InquiryResponseDTO.from(responseEntity))
                .build();
    }
}
