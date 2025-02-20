package com.enf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "tb_user")
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

  private String providerId;

  @ManyToOne
  @JoinColumn(name = "role_seq")
  private RoleEntity role;

  private LocalDate createAt;

  private LocalDate lastLoginAt;

}