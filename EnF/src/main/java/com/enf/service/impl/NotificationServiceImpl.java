package com.enf.service.impl;

import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();

  public SseEmitter createEmitter(HttpServletRequest request, Long userId) {

    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    emitters.put(userId, emitter);

    emitter.onCompletion(() -> emitters.remove(userId));
    emitter.onTimeout(() -> emitters.remove(userId));

    sendNotification(new NotificationDTO(userId, "connect"));
    return emitter;
  }

  public void sendNotification(NotificationDTO notification) {
    SseEmitter emitter = emitters.get(notification.getUserId());

    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name("notifications").data(notification.getMessage()));
      } catch (IOException e) {
        emitters.remove(notification.getUserId());
      }
    } else {
      log.info("emitters.get(userId): {}", emitters.get(notification.getUserId()));
      emitters.remove(notification.getUserId());
    }
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    log.info("onMessage >>>");
    try {
      NotificationDTO notification = mapper.readValue(message.getBody(), NotificationDTO.class);

      log.info("waringMessage.toString(): {}", notification.toString());
      sendNotification(notification);
    } catch (IOException e) {
      log.info("Exception {}", e.getMessage());
    }
  }
}