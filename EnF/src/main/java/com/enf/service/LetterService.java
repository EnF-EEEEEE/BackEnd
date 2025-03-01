package com.enf.service;

import com.enf.model.dto.request.letter.ReceiveLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface LetterService {

  ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter);

  ResultResponse receiveLetter(HttpServletRequest request, ReceiveLetterDTO receiveLetter);
}
