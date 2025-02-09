package com.enf.service;


import com.enf.model.dto.response.ResultResponse;

public interface AuthService {

  ResultResponse oAuthForKakao(String code);

}
