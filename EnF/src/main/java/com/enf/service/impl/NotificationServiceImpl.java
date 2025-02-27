package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.entity.NotificationEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.type.TokenType;
import com.enf.repository.NotificationRepository;
import com.enf.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final UserFacade userFacade;
  private final NotificationRepository notificationRepository;
  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();
  private final RedisTemplate<String, String> redisTemplate;

  public SseEmitter createEmitter(HttpServletRequest request) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    emitters.put(user.getUserSeq(), emitter);

    emitter.onCompletion(() -> emitters.remove(user.getUserSeq()));
    emitter.onTimeout(() -> emitters.remove(user.getUserSeq()));

    log.info("사용자 {} SSE 구독 시작", user.getNickname());

    // 연결 확인을 위해 기본 메시지 전송
    sendNotification(new NotificationDTO(user.getUserSeq(), "connect"));

    // Redis에서 읽지 않은 알림을 가져와 즉시 전송
    sendPendingNotifications(user.getUserSeq(), emitter);

    return emitter;
  }

  /**
   * 사용자가 구독할 때 Redis에 저장된 미확인 알림을 즉시 전송
   */
  private void sendPendingNotifications(Long userSeq, SseEmitter emitter) {
    log.info("sending notifications for {} ", userSeq);
    String redisKey = "notifications_" + userSeq;

    // Redis에서 모든 미확인 알림 가져오기
    List<String> pendingMessages = redisTemplate.opsForList().range(redisKey, 0, -1);

    if (pendingMessages != null && !pendingMessages.isEmpty()) {
      log.info("사용자 {}에게 {}개의 미확인 알림 전송", userSeq, pendingMessages.size());

      for (String message : pendingMessages) {
        log.info("message {}", message);
        try {
          emitter.send(SseEmitter.event().name("notifications").data(message));
        } catch (IOException e) {
          log.error("SSE 전송 오류: {}", e.getMessage());
          return;
        }
      }
      // 전송 완료 후 Redis에서 삭제
      redisTemplate.delete(redisKey);
    }
  }

  /**
   * 실시간 알림 전송 로직
   */
  public void sendNotification(NotificationDTO notification) {
    SseEmitter emitter = emitters.get(notification.getUserSeq());

    if (emitter != null) {
      try {
        // 사용자가 구독 중이라면 즉시 전송
        emitter.send(SseEmitter.event().name("notifications").data(notification.getMessage()));
      } catch (IOException e) {
        log.error("SSE 전송 오류: {}", e.getMessage());
      }
    } else {
      log.info("사용자가 구독 중이 아님. Redis 및 DB에 알림 저장: {}", notification.getMessage());

      // Redis에 저장
      redisTemplate.opsForList().leftPush("notifications_" + notification.getUserSeq(), notification.getMessage());

      // DB에 저장
      NotificationEntity notificationEntity = NotificationEntity.builder()
          .userId(notification.getUserSeq())
          .message(notification.getMessage())
          .isSent(false)
          .createdAt(LocalDateTime.now())
          .build();
      notificationRepository.save(notificationEntity);
    }
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    log.info("onMessage >>>");
    try {
      NotificationDTO notification = mapper.readValue(message.getBody(), NotificationDTO.class);

      log.info("userSeq : {} notification: {}", notification.getUserSeq(), notification.getMessage());
      sendNotification(notification);
    } catch (IOException e) {
      log.info("Exception {}", e.getMessage());
    }
  }
}
