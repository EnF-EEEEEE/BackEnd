package com.enf.email.service;

public interface EmailService {

    public void sendMenteeLetterArrivedEmail(String email, String nickname);

    public void sendMentorLetterArrivedEmail(String email, String nickname);

    public void sendOneDayNoticeEmail(String email, String nickname);
}
