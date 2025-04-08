package com.enf.domain.model.dto.request.inquiry;

import com.enf.domain.entity.InquiryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryPageResponseDTO {
    private List<InquiryListDTO> inquiries;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    public static InquiryPageResponseDTO from(Page<InquiryEntity> inquiriesPage) {
        List<InquiryListDTO> inquiriesList = inquiriesPage.getContent().stream()
                .map(InquiryListDTO::from)
                .collect(Collectors.toList());

        return InquiryPageResponseDTO.builder()
                .inquiries(inquiriesList)
                .currentPage(inquiriesPage.getNumber())
                .totalItems(inquiriesPage.getTotalElements())
                .totalPages(inquiriesPage.getTotalPages())
                .build();
    }
}
