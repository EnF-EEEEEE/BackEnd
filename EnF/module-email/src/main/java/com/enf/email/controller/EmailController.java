package com.enf.email.controller;

import com.enf.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text) {

        emailService.sendSimpleEmail(to, subject, text);
        return ResponseEntity.ok("이메일이 성공적으로 전송되었습니다");
    }

    @PostMapping("/send-html")
    public ResponseEntity<String> sendHtmlEmail(
            @RequestParam String to,
            @RequestParam String subject) {

        emailService.sendHtmlEmail(to, subject);
        return ResponseEntity.ok("HTML 이메일이 성공적으로 전송되었습니다");
    }
}
