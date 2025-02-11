package com.enf.entity;

import com.enf.model.type.AgeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String email;

  private String password;

  private String nickname;

  private String tel;

  @Enumerated(EnumType.STRING)
  private AgeType ageType;

}
