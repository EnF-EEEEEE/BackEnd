package com.enf.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userSeq;

  @ManyToOne
  @JoinColumn(name = "bird_seq")
  private BirdEntity bird;

  @ManyToOne
  @JoinColumn(name = "role_seq")
  private RoleEntity role;

  @ManyToOne
  @JoinColumn(name = "category_seq")
  private CategoryEntity category;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  private String provider;

  @Column(nullable = false)
  private String providerId;

  @Column(nullable = false)
  private int quota;

  @Column(nullable = false)
  private LocalDateTime createAt;

  private LocalDateTime lastLoginAt;

  private LocalDateTime deleteAt;

  private String refreshToken;

}