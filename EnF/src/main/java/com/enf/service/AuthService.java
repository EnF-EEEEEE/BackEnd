package com.enf.service;


import com.enf.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService {

  ResultResponse oAuthForKakao(HttpServletResponse response, String code);

  String loginTokenGenerator(ResultResponse resultResponse);

  UserDetails loadUserById(Long userSeq) throws UsernameNotFoundException;
}
