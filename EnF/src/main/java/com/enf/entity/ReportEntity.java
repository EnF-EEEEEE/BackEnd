package com.enf.entity;

import com.enf.model.type.ReportCategory;
import com.enf.model.type.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "report")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportSeq;

    @Enumerated(EnumType.STRING)
    private ReportCategory category; // 신고 카테고리

    @ManyToOne
    @JoinColumn(name = "letter_seq")
    private LetterEntity letter; // 신고 대상 편지

    @ManyToOne
    @JoinColumn(name = "reporter_seq")
    private UserEntity reporter; // 신고자

    @Enumerated(EnumType.STRING)
    private ReportStatus status; // 처리 상태

    private LocalDateTime createAt; // 신고 생성 시간

}