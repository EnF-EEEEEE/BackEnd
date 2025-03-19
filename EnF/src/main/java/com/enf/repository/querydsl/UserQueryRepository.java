package com.enf.repository.querydsl;

import static java.util.Optional.ofNullable;

import com.enf.entity.QThrowLetterEntity;
import com.enf.entity.QUserEntity;
import com.enf.entity.UserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * QueryDSL을 활용한 사용자 조회 Repository
 */

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  QUserEntity user = QUserEntity.userEntity;
  QThrowLetterEntity throwLetter = QThrowLetterEntity.throwLetterEntity;

  /**
   * 멘토 조회 메서드
   *
   * 우선순위에 따라 멘토를 조회한다.
   * 1순위: 새 유형과 카테고리가 모두 일치하는 멘토
   * 2순위: 카테고리만 일치하는 멘토
   * 3순위: 새 유형만 일치하는 멘토
   * 4순위: (추가 예정)
   * 5순위: 랜덤 멘토
   * 6순위: 관리자 (추가 예정)
   *
   * @param birdName 새 유형
   * @param categoryName 카테고리명
   * @param letterStatusSeq 편지를 넘긴 이력 확인을 위한 식별자
   * @return 조건에 맞는 사용자 엔티티 (멘토) 또는 null
   */
  public UserEntity getMentor(String birdName, String categoryName, Long letterStatusSeq) {
    log.info("birdName: {} categoryName: {}", birdName, categoryName);
    return fetchUser(buildConditions(birdName, categoryName, letterStatusSeq))
        .orElseGet(() -> fetchUser(buildConditions(null, categoryName, letterStatusSeq))
            .orElseGet(() -> fetchUser(buildConditions(birdName, null, letterStatusSeq))
                .orElseGet(() -> randomUser(buildConditions(null, null, letterStatusSeq))
                    .orElseGet(this::getAdminUser))));
  }

  /**
   * 조건을 기반으로 멘토를 조회하는 메서드
   *
   * @param builder QueryDSL의 BooleanBuilder (검색 조건)
   * @return 조건에 맞는 첫 번째 사용자 엔티티 (Optional)
   */
  private Optional<UserEntity> fetchUser(BooleanBuilder builder) {
    log.info(builder.toString());
    return ofNullable(jpaQueryFactory
        .selectFrom(user)
        .where(builder)
        .orderBy(user.quota.desc())
        .fetchFirst());
  }

  /**
   * 랜덤 멘토를 조회하는 메서드
   *
   * @param builder QueryDSL의 BooleanBuilder (검색 조건)
   * @return 조건에 맞는 랜덤 멘토 (첫 번째 결과 반환)
   */
  private Optional<UserEntity> randomUser(BooleanBuilder builder) {
    return ofNullable(jpaQueryFactory
        .selectFrom(user)
        .where(builder)
        .orderBy(user.quota.desc())
        .fetchFirst());
  }

  /**
   * 카테고리명에 따른 검색 조건을 반환하는 메서드
   *
   * @param user 사용자 엔티티
   * @param categoryName 카테고리명
   * @return BooleanExpression (해당 카테고리가 true인 조건)
   */
  private BooleanExpression getCategoryPredicate(QUserEntity user, String categoryName) {
    if (categoryName == null || categoryName.isEmpty()) return null;
    return switch (categoryName) {
      case "커리어" -> user.category.career.eq(true);
      case "마음건강" -> user.category.mental.eq(true);
      case "대인관계" -> user.category.relationship.eq(true);
      case "사랑" -> user.category.love.eq(true);
      case "삶의 방향" -> user.category.life.eq(true);
      case "자산관리" -> user.category.finance.eq(true);
      case "독립" -> user.category.housing.eq(true);
      case "그 외 기타" -> user.category.other.eq(true);
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
  /*
   * 멘토 조회 조건을 생성하는 메서드
   *
   * @param birdName 새 유형
   * @param categoryName 카테고리명
   * @param letterStatusSeq 편지를 넘긴 이력을 조회하기 위한 식별자
   * @return BooleanBuilder (QueryDSL 검색 조건)
   */
  private BooleanBuilder buildConditions(String birdName, String categoryName, Long letterStatusSeq) {
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(user.role.roleName.eq("MENTOR"));
    builder.and(user.quota.goe(1));

    if (birdName != null) {
      builder.and(user.bird.birdName.eq(birdName));
    }
    if (categoryName != null) {
      builder.and(getCategoryPredicate(user, categoryName));
    }

    if (letterStatusSeq != null) {
      List<UserEntity> userList = getUsersWhoThrownLetter(letterStatusSeq);
      builder.and(user.notIn(userList));
    }

    return builder;
  }

  /**
   * 특정 편지를 넘긴 사용자 목록을 조회하는 메서드
   *
   * @param letterId 편지 ID
   * @return 해당 편지를 넘긴 사용자 리스트
   */
  public List<UserEntity> getUsersWhoThrownLetter(Long letterId) {
    return jpaQueryFactory
        .select(throwLetter.throwUser)
        .from(throwLetter)
        .where(throwLetter.letterStatus.letterStatusSeq.eq(letterId))
        .fetch();
  }

  public UserEntity getAdminUser() {
    return jpaQueryFactory
        .selectFrom(user)
        .where(user.nickname.eq("지미니짱짱"))
        .fetchFirst();
  }
}
