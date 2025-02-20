package com.enf.entity;

import com.enf.model.type.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userSeq;

  private String email;

  private String nickname;

  private String age;

  private String provider;

  private String providerId;

  @Enumerated(EnumType.STRING)
  private RoleType role;

  private LocalDateTime createAt;

  private LocalDateTime lastLoginAt;

}
