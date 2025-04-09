package com.enf.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${spring.mail.username}")
    private String USER_NAME;

    @Value("${spring.mail.password}")
    private String PASSWORD;
    @Value("${spring.mail.port}")
    private int PORT;
    @Value("${spring.mail.host}")
    private String HOST;

    @Bean
    public JavaMailSender javaMailSender() {
        System.out.println("HOST : "+HOST);
        System.out.println("PORT : "+PORT);
        System.out.println("USER_NAME : "+USER_NAME);
        System.out.println("PASSWORD : "+PASSWORD);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);
        mailSender.setUsername(USER_NAME);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}