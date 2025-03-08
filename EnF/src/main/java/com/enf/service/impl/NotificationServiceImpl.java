package com.enf.service.impl;

import com.enf.component.facade.LetterFacade;
import com.enf.component.facade.UserFacade;
import com.enf.entity.NotificationEntity;
import com.enf.entity.NotificationStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.notification.NotificationDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.notification.Notification;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
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

  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();
  private final Long TIME_OUT = 30 * 60 * 1000L;

  /**
   * SSE 연결을 생성하고 사용자를 구독 처리
   *
   * @param request HTTP 요청 객체
   * @return SSE 연결 객체
   */
  public SseEmitter createEmitter(HttpServletRequest request) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    SseEmitter emitter = new SseEmitter(TIME_OUT);

    emitters.put(user.getUserSeq(), emitter);
    emitter.onCompletion(() -> emitters.remove(user.getUserSeq()));
    emitter.onTimeout(() -> emitters.remove(user.getUserSeq()));

    log.info("사용자 {} SSE 구독 시작", user.getNickname());

    // 구독 성공 알림 전송
    try {
      emitter.send(SseEmitter.event().name("notifications").data(user.getNickname() + "구독 성공"));
    } catch (IOException e) {
      log.error("사용자 {} SSE 전송 오류: {}", user.getNickname(), e.getMessage());
    }

    // 미확인 알림 전송
    sendPendingNotifications(user);

    return emitter;
  }

  @Override
  public ResultResponse notificationList(HttpServletRequest request) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    List<NotificationEntity> notificationList = letterFacade.getNotificationList(user.getUserSeq());
    List<Notification> notifications = notificationList != null
        ? Notification.of(notificationList)
        : null;

    return new ResultResponse(SuccessResultType.SUCCESS_GET_NOTIFICATION, notifications);
  }

  /**
   * 사용자가 SSE를 구독할 때, 저장된 미확인 알림을 Redis를 통해 전송
   *
   * @param user 사용자
   */
  private void sendPendingNotifications(UserEntity user) {
    List<NotificationStatusEntity> notificationStatus = letterFacade.getNotificationStatusList(user.getUserSeq());
    if (notificationStatus.isEmpty()) {
      log.info("사용자 {} : 미확인 알림 없음", user.getNickname());
      return;
    }
    log.info("사용자 {} : 미확인 알림 : {}", user.getNickname(), notificationStatus.size());
    sendNotification(user.getUserSeq());
  }

  /**
   * 실시간 알림을 SSE를 통해 전송하거나, 구독자가 없을 경우 DB에 저장
   *
   * @param userSeq 사용자 일련번호
   */
  public void sendNotification(Long userSeq) {
    SseEmitter emitter = emitters.get(userSeq);

    if (emitter == null) {
      log.info("사용자 {} SSE 미구독 → 알림을 DB에 저장", userSeq);
      letterFacade.saveNotificationStatus(userSeq);
      return;
    }

    try {
      emitter.send(SseEmitter.event().name("notifications").data("알림도착"));
    } catch (IOException e) {
      log.error("사용자 {} SSE 전송 오류: {}, 알림을 DB에 저장", userSeq, e.getMessage());
      letterFacade.saveNotificationStatus(userSeq);
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

      letterFacade.saveNotification(NotificationDTO.toEntity(notification));
      sendNotification(notification.getUserSeq());
    } catch (IOException e) {
      log.error("Redis Pub/Sub 메시지 파싱 오류: {}", e.getMessage(), e);
    }
  }
}