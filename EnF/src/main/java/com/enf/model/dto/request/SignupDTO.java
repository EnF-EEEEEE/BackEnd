package com.enf.model.dto.request;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class SignupDTO {

  @Email(message = "이메일 형식이 아닙니다.")
  @JsonProperty("email")
  private String email;

  @NotEmpty(message = "비밀번호를 입력해주세요.")
  @JsonProperty("password")
  private String password;

  @NotEmpty(message = "닉네임을 입력해주세요.")
  @JsonProperty("nickname")
  private String nickname;

  @NotEmpty(message = "전화번호를 입력해주세요")
  @JsonProperty("tel")
  private String tel;

  @NotEmpty(message = "생년월일")
  @JsonProperty("birthday")
  private String birthday;

  @JsonCreator
  public SignupDTO(String email, String password, String nickname, String tel) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.tel = tel;
  }
}
