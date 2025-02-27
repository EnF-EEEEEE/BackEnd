package com.enf.service;

import com.enf.model.dto.request.user.AdditionalInfoDTO;
import com.enf.model.dto.request.user.UpdateNicknameDTO;
import com.enf.model.dto.request.user.UserCategoryDTO;
import com.enf.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

  ResultResponse checkNickname(String nickname);

  ResultResponse additionalInfo(HttpServletRequest request, HttpServletResponse response,
      AdditionalInfoDTO additionalInfoDTO);

  ResultResponse userInfo(HttpServletRequest request);

  ResultResponse updateNickname(HttpServletRequest request, UpdateNicknameDTO nickname);

  ResultResponse updateCategory(HttpServletRequest request, UserCategoryDTO userCategory);
}
