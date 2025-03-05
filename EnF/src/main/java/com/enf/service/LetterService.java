package com.enf.service;

import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface LetterService {

  ResultResponse sendLetter(HttpServletRequest request, SendLetterDTO sendLetter);

  ResultResponse replyLetter(HttpServletRequest request, ReplyLetterDTO replyLetter);

  ResultResponse getAllLetterList(HttpServletRequest request, int pageNumber);

  ResultResponse getPendingLetterList(HttpServletRequest request, int pageNumber);

  ResultResponse getSaveLetterList(HttpServletRequest request, int pageNumber);

  ResultResponse saveLetter(HttpServletRequest request, Long letterStatusSeq);

  ResultResponse getLetterDetails(HttpServletRequest request, Long letterStatusSeq);

  ResultResponse throwLetter(HttpServletRequest request, Long letterStatusSeq);

  ResultResponse thanksToMentor(HttpServletRequest request, Long letterSeq);

  ResultResponse getThrowLetterCategory(HttpServletRequest request);
}
