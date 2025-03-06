package com.enf.repository.querydsl;

import com.enf.entity.QUserEntity;
import com.enf.entity.UserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * QueryDSL을 활용한 사용자 조회 Repository
 */
@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final QUserEntity user = QUserEntity.userEntity;

  /**
   * 특정 조건에 맞는 사용자 조회
   *
   * @param birdName     새 유형
   * @param categoryName 카테고리명
   * @return 조건에 맞는 사용자 엔티티
   */
  public UserEntity getSendUser(String birdName, String categoryName) {
    // 1순위: 새 유형과 카테고리가 모두 일치하는 사용자
    UserEntity result = fetchUser(user.bird.birdName.eq(birdName), getCategoryPredicate(user, categoryName));
    if (result != null) return result;

    // 2순위: 카테고리만 일치하는 사용자
    result = fetchUser(getCategoryPredicate(user, categoryName));
    if (result != null) return result;

    // 3순위: 새 유형만 일치하는 사용자
    return fetchUser(user.bird.birdName.eq(birdName));
  }

  /**
   * 주어진 조건을 기반으로 사용자 조회
   *
   * @param conditions BooleanExpression (검색 조건)
   * @return 조건에 맞는 첫 번째 사용자 엔티티 또는 null
   */
  private UserEntity fetchUser(BooleanExpression... conditions) {
    return jpaQueryFactory
        .selectFrom(user)
        .where(conditions)
        .fetchFirst();
  }

  /**
   * 카테고리명에 따른 검색 조건 반환
   *
   * @param user        사용자 엔티티
   * @param categoryName 카테고리명
   * @return BooleanExpression (검색 조건)
   */
  private BooleanExpression getCategoryPredicate(QUserEntity user, String categoryName) {
    if (categoryName == null || categoryName.isEmpty()) return null;

    return switch (categoryName) {
      case "career" -> user.category.career.isTrue();
      case "mental" -> user.category.mental.isTrue();
      case "relationship" -> user.category.relationship.isTrue();
      case "love" -> user.category.love.isTrue();
      case "life" -> user.category.life.isTrue();
      case "finance" -> user.category.finance.isTrue();
      case "housing" -> user.category.housing.isTrue();
      case "other" -> user.category.other.isTrue();
      default -> null;
    };
  }

  /**
   * 이번 주(월요일부터 현재까지) 로그인한 회원 수 조회
   *
   * @return 이번 주 로그인한 회원 수
   */
  public Long countWeeklyActiveUsers() {
    // 이번 주 월요일 시작 시간 계산
    LocalDate today = LocalDate.now();
    LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDateTime startOfWeek = monday.atStartOfDay();

    return jpaQueryFactory
            .select(user.count())
            .from(user)
            .where(lastLoginAfter(startOfWeek))
            .fetchOne();
  }

  /**
   * 날짜별 로그인한 회원 수 조회
   *
   * @param date 조회할 날짜
   * @return 해당 날짜에 로그인한 회원 수
   */
  public Long countDailyActiveUsers(LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

    return jpaQueryFactory
            .select(user.count())
            .from(user)
            .where(lastLoginBetween(startOfDay, endOfDay))
            .fetchOne();
  }

  /**
   * 특정 시간 이후 로그인한 사용자 필터링 조건
   *
   * @param dateTime 기준 시간
   * @return BooleanExpression
   */
  private BooleanExpression lastLoginAfter(LocalDateTime dateTime) {
    return dateTime != null ? user.lastLoginAt.goe(dateTime) : null;
  }

  /**
   * 특정 기간 내 로그인한 사용자 필터링 조건
   *
   * @param startDateTime 시작 시간
   * @param endDateTime 종료 시간
   * @return BooleanExpression
   */
  private BooleanExpression lastLoginBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return startDateTime != null && endDateTime != null ?
            user.lastLoginAt.between(startDateTime, endDateTime) : null;
  }
}