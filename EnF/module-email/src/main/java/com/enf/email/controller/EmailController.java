package com.enf.email.controller;

import com.enf.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email/send")
@RequiredArgsConstructor
@Slf4j
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/mentee-letter-arrived")
    public ResponseEntity<String> sendMenteeLetterArrivedEmail(
            @RequestParam String email,
            @RequestParam String nickname) {
        log.info("멘티 편지 도착 이메일 요청: {}", email);
        emailService.sendMenteeLetterArrivedEmail(email, nickname);
        return ResponseEntity.ok("멘토에게 편지 도착 알림 이메일을 전송합니다.");
    }

    @PostMapping("/mentor-letter-arrived")
    public ResponseEntity<String> sendMentorLetterArrivedEmail(
            @RequestParam String email,
            @RequestParam String nickname) {

        log.info("멘토 편지 도착 이메일 요청: {}", email);
        emailService.sendMentorLetterArrivedEmail(email, nickname);
        return ResponseEntity.ok("멘티에게 답장 도착 알림 이메일을 전송합니다.");
    }

    @PostMapping("/one-day-notice")
    public ResponseEntity<String> sendOneDayNoticeEmail(
            @RequestParam String email,
            @RequestParam String nickname) {
        log.info("하루 전 알림 이메일 요청: {}", email);
        emailService.sendOneDayNoticeEmail(email, nickname);
        return ResponseEntity.ok("멘티에게 답장 도착 알림 이메일을 전송합니다.");
    }
}

/*
    클라이언트측 요청 내용

    @PostMapping("/api/v1/email/send/mentee-letter-arrived")
    String sendMenteeLetterArrivedEmail(@RequestParam("to") String to);

    @PostMapping("/api/v1/email/send/mentor-letter-arrived")
    String sendMentorLetterArrivedEmail(@RequestParam("to") String to);

    @PostMapping("/api/v1/email/send/one-day-notice")
    String sendOneDayNoticeEmail(@RequestParam("to") String to);
 */