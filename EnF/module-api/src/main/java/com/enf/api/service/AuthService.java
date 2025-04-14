package com.enf.api.service;


import com.enf.domain.model.dto.request.auth.WithdrawalDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService {

  ResultResponse oAuthForKakao(HttpServletRequest request, HttpServletResponse response, String code);

  UserDetails loadUserById(Long userSeq) throws UsernameNotFoundException;

  ResultResponse reissueToken(HttpServletRequest request, HttpServletResponse response);

  ResultResponse withdrawal(HttpServletRequest request, WithdrawalDTO withdrawalDTO);
}
