package com.enf.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity(name = "email_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class EmailLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailLogId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean sendSuccess;

    @Column(nullable = false)
    private LocalDateTime sendAt;
}
