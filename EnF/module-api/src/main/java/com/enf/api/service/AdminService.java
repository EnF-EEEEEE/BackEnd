package com.enf.api.service;

import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<Map<String, Object>> getDashboardData();
}
