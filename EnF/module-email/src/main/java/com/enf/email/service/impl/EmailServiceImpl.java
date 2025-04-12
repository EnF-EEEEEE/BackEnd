package com.enf.email.service.impl;

import com.enf.domain.entity.EmailLogEntity;
import com.enf.domain.model.dto.request.email.SendEmailDTO;
import com.enf.domain.repository.EmailLogRepository;
import com.enf.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 멘티에게 편지 도착 알림 이메일을 전송합니다.
     *
     * @param email 수신자 이메일
     * @return 이메일 전송 결과 메시지
     */
    @Async
    public void sendMenteeLetterArrivedEmail(String email, String nickname) {
        EmailLogEntity emailLog = null;
        try {
            String htmlTemplate = loadHtmlTemplate("templates/email/mentee-letter-arrived.html");
            String subject = nickname + "님, 새로운 답장이 도착했습니다";

            htmlTemplate = replacePlaceholders(htmlTemplate, Map.of(
                    "nickname", nickname
            ));

            sendHtmlEmail(email, subject, htmlTemplate);
            emailLog = SendEmailDTO.builder()
                    .email(email)
                    .sendSuccess(true)
                    .build()
                    .to();
        } catch (Exception e) {
            log.error("멘티 편지 도착 이메일 전송 실패: {}", e.getMessage(), e);
            emailLog = SendEmailDTO.builder()
                    .email(email)
                    .sendSuccess(false)
                    .build()
                    .to();
        } finally {
            emailLogRepository.save(Objects.requireNonNull(emailLog));
        }
    }

    /**
     * 멘토에게 편지 도착 알림 이메일을 전송합니다.
     *
     * @param email 수신자 이메일
     * @return 이메일 전송 결과 메시지
     */
    @Override
    @Async
    public void sendMentorLetterArrivedEmail(String email, String nickname) {
        EmailLogEntity emailLog = null;
        try {
            String htmlTemplate = loadHtmlTemplate("templates/email/mentor-letter-arrived.html");
            String subject = nickname + "님, 새로운 고민이 도착했습니다";

            htmlTemplate = replacePlaceholders(htmlTemplate, Map.of(
                    "nickname", nickname
            ));

            sendHtmlEmail(email, subject, htmlTemplate);
            emailLog = SendEmailDTO.builder()
                    .email(email)
                    .sendSuccess(true)
                    .build()
                    .to();
        } catch (Exception e) {
            log.error("멘토 편지 도착 이메일 전송 실패: {}", e.getMessage(), e);
            emailLog = SendEmailDTO.builder()
                    .email(email)
                    .sendSuccess(false)
                    .build()
                    .to();
        } finally {
            emailLogRepository.save(Objects.requireNonNull(emailLog));
        }
    }

    /**
     * 하루 전 알림 이메일을 전송합니다.
     *
     * @param email 수신자 이메일
     * @return 이메일 전송 결과 메시지
     */
    @Override
    @Async
    public void sendOneDayNoticeEmail(String email, String nickname) {
        EmailLogEntity emailLog = null;
        try {
            String htmlTemplate = loadHtmlTemplate("templates/email/one-day-notice.html");
            String subject = nickname + "님 멘티가 답장을 기다리고 있습니다.";

            htmlTemplate = replacePlaceholders(htmlTemplate, Map.of(
                    "nickname", nickname
            ));
            sendHtmlEmail(email, subject, htmlTemplate);
            emailLog = SendEmailDTO.builder()
                    .email(email)
                    .sendSuccess(true)
                    .build()
                    .to();
        } catch (Exception e) {
            log.error("하루 전 알림 이메일 전송 실패: {}", e.getMessage(), e);
            emailLog = SendEmailDTO.builder()
                    .email(email)
                    .sendSuccess(false)
                    .build()
                    .to();
        } finally {
            emailLogRepository.save(Objects.requireNonNull(emailLog));
        }
    }

    /**
     * HTML 이메일을 전송합니다.
     */
    private void sendHtmlEmail(String email, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true는 HTML 콘텐츠를 활성화

        mailSender.send(message);
        log.info("이메일 전송 성공: {} -> {}", subject, email);
    }

    /**
     * HTML 템플릿 파일을 로드합니다.
     */
    private String loadHtmlTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    /**
     * 템플릿의 플레이스홀더를 실제 데이터로 대체합니다.
     */
    private String replacePlaceholders(String template, Map<String, String> placeholders) {
        String result = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }


}





