
package com.enf.entity;

import com.enf.model.type.PenaltyType;
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


@Getter
@Builder
@Entity(name = "penalty")
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long penaltySeq;                // id

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private UserEntity user;                //user

    @Enumerated(EnumType.STRING)
    private PenaltyType penaltyType;        // 제재 타입

    private LocalDateTime penaltyAt;        // 제재 날자
}

/**
 * 1: 경고
 * 2: 7일 편지 받기 & 보내기 제한
 * 3: 30일 이용 정지
 * 4: 영구 정지
 */
