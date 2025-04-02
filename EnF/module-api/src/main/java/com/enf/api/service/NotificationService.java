package com.enf.api.service;

import com.enf.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService extends MessageListener {

  SseEmitter createEmitter(HttpServletRequest request);

  ResultResponse notificationList(HttpServletRequest request);
}
