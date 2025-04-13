package com.enf.api.service.impl;

import com.enf.api.feign.EmailFeignClient;
import com.enf.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailFeignClient emailFeignClient;

    /**
     * 멘티가 편지 작성시 멘토에게 답장 요청을 위한 이메일을 전송합니다.
     * Feign 클라이언트를 사용하여 이메일을 전송합니다.
     * @param email  수신자 이메일 주소
     * @param nickname  사용자 닉네임
     * @return 결과 응답 객체
     */
    @Override
    public void sendMenteeLetterArrivedEmail(String email,String nickname) {
        try {
            log.info("Sending email to {}", email);
            emailFeignClient.sendMenteeLetterArrivedEmail(email,nickname);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 멘토가 답장 작성시 멘티에게 확인 요청을 위한 이메일을 전송합니다.
     * Feign 클라이언트를 사용하여 이메일을 전송합니다.
     * @param email  수신자 이메일 주소
     * @param nickname  사용자 닉네임
     * @return 결과 응답 객체
     */
    @Override
    public void sendMentorLetterArrivedEmail(String email,String nickname) {
        try {
            log.info("Sending email to {}", email);
            emailFeignClient.sendMenteeLetterArrivedEmail(email,nickname);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 멘토가 받은 편지중 답장 기한이 1일남은 편지가 있을경우 답장 요청을 위한 이메일을 전송합니다.
     * Feign 클라이언트를 사용하여 이메일을 전송합니다.
     * @param email  수신자 이메일 주소
     * @param nickname  사용자 닉네임
     * @return 결과 응답 객체
     */
    @Override
    public void sendOneDayNoticeEmail(String email,String nickname) {
        try {
            log.info("Sending email to {}", email);
            emailFeignClient.sendMenteeLetterArrivedEmail(email,nickname);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw e;
        }
    }
}
