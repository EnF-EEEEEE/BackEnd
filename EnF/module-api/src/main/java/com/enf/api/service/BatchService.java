package com.enf.api.service;

public interface BatchService {

  void sendNotificationToMentor(Long letterStatusSeq);

  void transferLetter(Long letterStatusSeq, Long transferSeq);

  void unlinkUser(Long userSeq);
}
