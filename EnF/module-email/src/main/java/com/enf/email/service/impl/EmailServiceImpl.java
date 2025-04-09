package com.enf.email.service.impl;

import com.enf.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private static final String htmlContent = "<!DOCTYPE html>\n" +
            "<html lang=\"ko\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "  <title>HTML 이메일 예시</title>\n" +
            "  <style>\n" +
            "    body {\n" +
            "      font-family: Arial, sans-serif;\n" +
            "      background-color: #f5f5dc; /* 베이지색 */\n" +
            "      margin: 0;\n" +
            "      padding: 20px;\n" +
            "    }\n" +
            "    .container {\n" +
            "      background-color: #ffffff;\n" +
            "      border-radius: 5px;\n" +
            "      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);\n" +
            "      padding: 20px;\n" +
            "    }\n" +
            "    h1 {\n" +
            "      color: #4caf50; /* 초록색 */\n" +
            "    }\n" +
            "    p {\n" +
            "      color: #4caf50; /* 초록색 */\n" +
            "      line-height: 1.6;\n" +
            "    }\n" +
            "    .button {\n" +
            "      display: inline-block;\n" +
            "      background-color: #4caf50; /* 초록색 */\n" +
            "      color: white;\n" +
            "      padding: 10px 20px;\n" +
            "      text-decoration: none;\n" +
            "      border-radius: 5px;\n" +
            "      margin-top: 10px;\n" +
            "    }\n" +
            "    .footer {\n" +
            "      margin-top: 20px;\n" +
            "      font-size: 12px;\n" +
            "      color: #888888;\n" +
            "    }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div class=\"container\">\n" +
            "  <h1>안녕하세요!</h1>\n" +
            "  <p>이것은 HTML 이메일의 예시입니다. 이메일을 통해 정보를 전달할 수 있습니다.</p>\n" +
            "  <p>편지 확인하기 버튼을 클릭하여 Dear Birdy를 방문해 주세요.</p>\n" +
            "  <p><a href=\"https://www.dearbirdy.xyz/letters\" class=\"button\">편지 확인하기</a></p>\n" +
            "  <div class=\"footer\">\n" +
            "    <p>이 이메일은 자동으로 생성되었습니다. 회신하지 마세요.</p>\n" +
            "  </div>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>\n";

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        //setter을 사용한건 지원 스팩이라서 사용했습니다.
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try{
            mailSender.send(message);
            log.info("이메일 전송 성공: {}", to);
        }catch (Exception e){
            log.error("이메일 전송 실패 : {}", e.getMessage(), e);
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML 이메일 전송 성공: {}", to);

        } catch (MessagingException e) {
            log.error("HTML 이메일 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("HTML 이메일 전송에 실패했습니다", e);
        }
    }
}
