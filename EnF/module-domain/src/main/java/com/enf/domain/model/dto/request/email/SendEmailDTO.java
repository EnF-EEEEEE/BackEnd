package com.enf.domain.model.dto.request.email;

import com.enf.domain.entity.EmailLogEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class SendEmailDTO {
    private String email;
    private boolean sendSuccess;

    public EmailLogEntity to() {
        return EmailLogEntity.builder()
                .email(email)
                .sendSuccess(sendSuccess)
                .sendAt(LocalDateTime.now())
                .build();
    }
}
