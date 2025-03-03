package com.enf.repository.querydsl;

import com.enf.entity.QQuotaEntity;
import com.enf.entity.QThrowLetterEntity;
import com.enf.entity.QUserEntity;
import com.enf.entity.UserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * QueryDSL을 활용한 사용자 조회 Repository
 */
@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  QUserEntity user = QUserEntity.userEntity;
  QQuotaEntity quota = QQuotaEntity.quotaEntity;
  QThrowLetterEntity throwLetter = QThrowLetterEntity.throwLetterEntity;

  /**
   * 특정 조건에 맞는 사용자 조회
   *
   * @param birdName     새 유형
   * @param categoryName 카테고리명
   * @return 조건에 맞는 사용자 엔티티
   */
  public UserEntity getMentor(String birdName, String categoryName) {

    // 1순위: 새 유형과 카테고리가 모두 일치하는 사용자
    // 2순위: 카테고리만 일치하는 사용자
    // 3순위: 새 유형만 일치하는 사용자
    // 4순위: 추가 예정
    // 5순위: 랜덤
    // 6순위: 관리자

    return fetchUser(user.bird.birdName.eq(birdName), getCategoryPredicate(user, categoryName), getQuotaPredicate(quota))
        .or(() -> fetchUser(getCategoryPredicate(user, categoryName), getQuotaPredicate(quota)))
        .or(() -> fetchUser(user.bird.birdName.eq(birdName), getQuotaPredicate(quota)))
        .orElse(randomUser(getQuotaPredicate(quota)));
  }

  /**
   * 주어진 조건을 기반으로 사용자 조회
   *
   * @param conditions BooleanExpression (검색 조건)
   * @return 조건에 맞는 첫 번째 사용자 엔티티 또는 null
   */
  private Optional<UserEntity> fetchUser(BooleanExpression... conditions) {

    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(user)
            .join(quota).on(user.userSeq.eq(quota.user.userSeq))
            .where(user.role.roleName.eq("MENTOR"),
                user.ne(throwLetter.throwUser))
            .where(conditions)
            .orderBy(quota.quota.desc())
            .fetchFirst()
    );
  }

  private UserEntity randomUser(BooleanExpression... conditions) {

    return jpaQueryFactory
            .selectFrom(user)
            .join(quota).on(user.userSeq.eq(quota.user.userSeq))
            .where(user.role.roleName.eq("MENTOR"),
                user.ne(throwLetter.throwUser))
            .where(conditions)
            .orderBy(quota.quota.desc())
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
   * Quota 조건 반환
   */
  private BooleanExpression getQuotaPredicate(QQuotaEntity quota) {
    return quota.quota.goe(1); // quota >= 1 조건 추가
  }
}