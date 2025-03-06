package com.enf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "inquiry")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class InquiryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquirySeq;

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private UserEntity user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private LocalDateTime createAt;

    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private InquiryResponseEntity response;

    public enum InquiryStatus {
        PENDING, ANSWERED
    }

    // 문의 내용의 처음 10글자를 제목으로 설정하는 메서드
    public void setTitleFromContent() {
        if (content != null && !content.isEmpty()) {
            if (content.length() <= 10) {
                this.title = content;
            } else {
                this.title = content.substring(0, 10) + "...";
            }
        }
    }

    // 답변이 등록되면 상태를 변경하는 메서드
    public void markAsAnswered() {
        this.status = InquiryStatus.ANSWERED;
    }
}