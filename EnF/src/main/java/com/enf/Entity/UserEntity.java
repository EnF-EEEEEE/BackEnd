package com.enf.Entity;

import com.enf.model.type.GenerateType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
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
  private Long userId;

  @NotNull
  private String email;

  @NotNull
  private String nickname;

  @NotNull
  private String provider;

  @NotNull
  private String providerId;

  // 청년층과 시니어층을 구분하기 위한 Enum Type
  @Enumerated(EnumType.STRING)
  private GenerateType generateType;

}
