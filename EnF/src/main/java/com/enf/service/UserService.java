package com.enf.service;

import com.enf.model.dto.response.ResultResponse;

public interface UserService {

  ResultResponse checkNickname(String nickname);

}
