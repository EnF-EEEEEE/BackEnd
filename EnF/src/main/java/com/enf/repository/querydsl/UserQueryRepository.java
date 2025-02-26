package com.enf.repository.querydsl;

import com.enf.entity.QUserEntity;
import com.enf.entity.UserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}