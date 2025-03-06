package com.enf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "inquiry_response")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class InquiryResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseSeq;

    @OneToOne
    @JoinColumn(name = "inquiry_seq")
    private InquiryEntity inquiry;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "admin_seq")
    private UserEntity admin;

    private LocalDateTime createAt;
}