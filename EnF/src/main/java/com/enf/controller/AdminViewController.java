package com.enf.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Value("${DEV.AUTH.OAUTH.REGISTRATION.KAKAO.client-id}")
    private String kakaoClientId;

    private String adminRedirectUri ="http://localhost:8080/api/v1/admin/callback";

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectUri", adminRedirectUri);
        return "admin/login";  // /admin/login.html 페이지로 이동
    }

    @GetMapping("/inquiries")
    public String getInquiriesPage(HttpServletRequest request, HttpServletResponse response) {
        String access = request.getHeader("access");
        log.info("access: {}", access);
        response.addHeader("access", access);
        return "admin/inquiries";
    }

    @GetMapping("/inquiries/{id}")
    public String getInquiryDetailPage(@PathVariable Long id) {
        return "admin/inquiry-detail";
    }
}
