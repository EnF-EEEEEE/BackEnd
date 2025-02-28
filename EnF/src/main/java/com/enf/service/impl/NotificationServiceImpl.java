package com.enf.service.impl;

import com.enf.component.facade.LetterFacade;
import com.enf.component.facade.UserFacade;
import com.enf.entity.NotificationEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.type.TokenType;
import com.enf.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 실시간 알림 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final UserFacade userFacade;
  private final LetterFacade letterFacade;
  private final RedisTemplate<String, String> redisTemplate;

  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * SSE 연결을 생성하고 사용자를 구독 처리
   *
   * @param request HTTP 요청 객체
   * @return SSE 연결 객체
   */
  public SseEmitter createEmitter(HttpServletRequest request) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    emitters.put(user.getUserSeq(), emitter);
    emitter.onCompletion(() -> emitters.remove(user.getUserSeq()));
    emitter.onTimeout(() -> emitters.remove(user.getUserSeq()));

    log.info("사용자 {} SSE 구독 시작", user.getNickname());

    // 구독 성공 알림 전송
    sendNotification(NotificationDTO.subscribe(user));

    // 미확인 알림 전송
    sendPendingNotifications(user.getUserSeq());

    return emitter;
  }

  /**
   * 사용자가 SSE를 구독할 때, 저장된 미확인 알림을 Redis를 통해 전송
   *
   * @param userSeq 사용자 ID
   */
  private void sendPendingNotifications(Long userSeq) {
    List<NotificationEntity> notifications = letterFacade.findAllByUserSeq(userSeq);
    if (notifications.isEmpty()) {
      log.info("사용자 {}: 미확인 알림 없음", userSeq);
      return;
    }

    // 알림 개수에 따라 메시지 생성
    NotificationDTO notificationDTO = (notifications.size() == 1)
        ? NotificationDTO.of(userSeq, notifications.get(0))
        : NotificationDTO.of(userSeq, notifications.get(0), notifications.size() - 1);

    try {
      String message = mapper.writeValueAsString(notificationDTO);
      redisTemplate.convertAndSend("notifications", message);

      letterFacade.deleteAllByUserSeq(userSeq);
    } catch (JsonProcessingException e) {
      log.error("Redis 메시지 직렬화 오류: {}", e.getMessage(), e);
    }

  }

  /**
   * 실시간 알림을 SSE를 통해 전송하거나, 구독자가 없을 경우 DB에 저장
   *
   * @param notification 전송할 알림 객체
   */
  public void sendNotification(NotificationDTO notification) {
    SseEmitter emitter = emitters.get(notification.getUserSeq());

    if (emitter == null) {
      letterFacade.saveNotification(NotificationDTO.toEntity(notification));
      log.info("사용자 {} SSE 미구독 → 알림을 DB에 저장", notification.getUserSeq());
      return;
    }

    try {
      emitter.send(SseEmitter.event().name("notifications").data(notification.getMessage()));
    } catch (IOException e) {
      log.error("사용자 {} SSE 전송 오류: {}, 알림을 DB에 저장", notification.getUserSeq(), e.getMessage());
      letterFacade.saveNotification(NotificationDTO.toEntity(notification));
    }
  }

  /**
   * Redis Pub/Sub을 통해 수신된 메시지를 처리
   *
   * @param message  Redis에서 수신한 메시지 객체
   * @param pattern  수신된 메시지의 패턴
   */
  @Override
  public void onMessage(Message message, byte[] pattern) {
    log.info("Redis Pub/Sub 수신");

    try {
      NotificationDTO notification = mapper.readValue(message.getBody(), NotificationDTO.class);

      sendNotification(notification);
    } catch (IOException e) {
      log.error("Redis Pub/Sub 메시지 파싱 오류: {}", e.getMessage(), e);
    }
  }
}