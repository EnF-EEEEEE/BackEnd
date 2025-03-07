package com.enf.entity;


import com.enf.model.type.ReportStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 신고 처리에 대한 내용
 */
@Entity(name = "report_process")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ReportProcessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processSeq;

    @ManyToOne
    @JoinColumn(name = "report_seq")
    private ReportEntity report; // 처리 대상 신고

    @Enumerated(EnumType.STRING)
    private ReportStatus status; // 처리 상태

    private String note; // 처리 내용

    @ManyToOne
    @JoinColumn(name = "handler_seq")
    private UserEntity handler; // 처리자

    private LocalDateTime createAt; // 처리 시간

}
