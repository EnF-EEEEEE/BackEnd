package com.enf.service;

import com.enf.model.dto.request.SignupDTO;
import com.enf.model.dto.response.ResultResponse;

public interface AuthService {

  ResultResponse signup(SignupDTO signupDTO);

}
