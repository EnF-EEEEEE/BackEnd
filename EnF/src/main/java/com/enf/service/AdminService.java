package com.enf.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AdminService {
    ResponseEntity<Map<String, Object>> getDashboardData();
}
