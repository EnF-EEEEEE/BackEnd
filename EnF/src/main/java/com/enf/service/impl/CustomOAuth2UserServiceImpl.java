package com.enf.service.impl;

import com.enf.service.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class CustomOAuth2UserServiceImpl implements CustomOAuth2UserService {
    private final RestTemplate restTemplate = new RestTemplate();

}