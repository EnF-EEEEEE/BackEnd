package com.enf.service;


import com.enf.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService {

  ResultResponse oAuthForKakao(HttpServletRequest request, HttpServletResponse response, String code);

  UserDetails loadUserById(Long userSeq) throws UsernameNotFoundException;

  ResultResponse reissueToken(HttpServletRequest request, HttpServletResponse response);
}
