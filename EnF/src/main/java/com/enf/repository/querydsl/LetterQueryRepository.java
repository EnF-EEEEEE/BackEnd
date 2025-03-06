package com.enf.repository.querydsl;

import com.enf.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.enf.entity.QThrowLetterCategoryEntity.throwLetterCategoryEntity;

import com.enf.entity.LetterStatusEntity;
import com.enf.entity.QLetterStatusEntity;
import com.enf.entity.QThrowLetterCategoryEntity;
import com.enf.entity.ThrowLetterCategoryEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Slf4j
@Repository
@RequiredArgsConstructor
public class LetterQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QLetterEntity letter = QLetterEntity.letterEntity;
    private final QLetterStatusEntity letterStatus = QLetterStatusEntity.letterStatusEntity;
    QThrowLetterCategoryEntity letterCategory = throwLetterCategoryEntity;


    /**
     * 모든 편지 목록 조회 - 시간순 정렬 및 페이징
     *
     * @param pageable 페이징 정보
     * @return 편지 목록 페이징 결과
     */
    public Page<Map<String, Object>> getAllLetters(Pageable pageable) {
        // 모든 편지 조회 (기본 정보)
        List<LetterEntity> letterEntities = jpaQueryFactory
                .selectFrom(letter)
                .orderBy(letter.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 편지 수 조회
        long total = jpaQueryFactory
                .select(letter.count())
                .from(letter)
                .fetchOne() != null ? jpaQueryFactory
                .select(letter.count())
                .from(letter)
                .fetchOne() : 0L;

        // 각 편지에 대한 상태 정보 조회
        List<Map<String, Object>> letterMaps = new ArrayList<>();
        for (LetterEntity letterEntity : letterEntities) {
            // 기본 편지 정보로 맵 생성
            Map<String, Object> letterMap = createBaseLetterMap(letterEntity);

            // 편지가 멘티 편지인 경우
            LetterStatusEntity menteeLetterStatus = findLetterStatusByMenteeLetter(letterEntity.getLetterSeq());
            if (menteeLetterStatus != null) {
                updateMapWithMenteeLetterStatus(letterMap, menteeLetterStatus);
            }

            // 편지가 멘토 편지인 경우
            LetterStatusEntity mentorLetterStatus = findLetterStatusByMentorLetter(letterEntity.getLetterSeq());
            if (mentorLetterStatus != null) {
                updateMapWithMentorLetterStatus(letterMap, mentorLetterStatus);
            }

            letterMaps.add(letterMap);
        }

        return new PageImpl<>(letterMaps, pageable, total);
    }

    /**
     * 편지 기본 정보로 맵 생성
     */
    private Map<String, Object> createBaseLetterMap(LetterEntity letterEntity) {
        Map<String, Object> letterMap = new HashMap<>();

        // ID
        letterMap.put("id", letterEntity.getLetterSeq());

        // 제목
        letterMap.put("title", letterEntity.getLetterTitle());

        // 카테고리
        letterMap.put("category", letterEntity.getCategoryName());

        // 내용
        letterMap.put("content", letterEntity.getLetter());

        // 작성 시간
        letterMap.put("sentAt", letterEntity.getCreateAt());

        // 기본값 설정
        letterMap.put("sender", "Unknown");
        letterMap.put("receiver", "Unknown");
        letterMap.put("hasResponse", false);
        letterMap.put("responseAt", null);
        letterMap.put("isSaved", false);

        return letterMap;
    }

    /**
     * 편지 ID로 멘티 편지 상태 조회
     */
    private LetterStatusEntity findLetterStatusByMenteeLetter(Long letterSeq) {
        return jpaQueryFactory
                .selectFrom(letterStatus)
                .leftJoin(letterStatus.mentee).fetchJoin()
                .leftJoin(letterStatus.mentor).fetchJoin()
                .where(letterStatus.menteeLetter.letterSeq.eq(letterSeq))
                .fetchFirst();
    }

    /**
     * 편지 ID로 멘토 편지 상태 조회
     */
    private LetterStatusEntity findLetterStatusByMentorLetter(Long letterSeq) {
        return jpaQueryFactory
                .selectFrom(letterStatus)
                .leftJoin(letterStatus.mentee).fetchJoin()
                .leftJoin(letterStatus.mentor).fetchJoin()
                .where(letterStatus.mentorLetter.letterSeq.eq(letterSeq))
                .fetchFirst();
    }

    /**
     * 멘티 편지 상태 정보로 맵 업데이트
     */
    private void updateMapWithMenteeLetterStatus(Map<String, Object> letterMap, LetterStatusEntity status) {
        // 멘티가 발신자, 멘토가 수신자가 됨
        letterMap.put("sender", status.getMentee().getNickname());
        letterMap.put("receiver", status.getMentor().getNickname());

        // 멘티 편지에 대한 멘토의 답장이 있는지 확인
        if (status.getMentorLetter() != null) {
            letterMap.put("hasResponse", true);
            letterMap.put("responseAt", status.getMentorLetter().getCreateAt());
        }

        // 멘티의 저장 여부
        letterMap.put("isSaved", status.isMenteeSaved());
    }

    /**
     * 멘토 편지 상태 정보로 맵 업데이트
     */
    private void updateMapWithMentorLetterStatus(Map<String, Object> letterMap, LetterStatusEntity status) {
        // 멘토가 발신자, 멘티가 수신자가 됨
        letterMap.put("sender", status.getMentor().getNickname());
        letterMap.put("receiver", status.getMentee().getNickname());

        // 멘토 편지는 답장일 가능성이 높음 (멘티 편지가 있는지 확인)
        if (status.getMenteeLetter() != null) {
            letterMap.put("hasResponse", true);
            // 이 편지 자체가 응답이므로 responseAt은 별도로 설정하지 않음
        }

        // 여기서는 멘티의 저장 여부를 확인하므로 해당 멘토 편지를 멘티가 저장했는지 확인
        letterMap.put("isSaved", status.isMenteeSaved());
    }

  /**
   * 멘티 또는 멘토의 편지 목록 조회 (페이징 지원)
   *
   * 1. 사용자 역할(멘티 또는 멘토)에 따라 적절한 필터링을 수행한다.
   * 2. `letterListType` 파라미터를 기반으로 추가적인 조건을 적용한다.
   *    - "all"     : 모든 편지 조회
   *    - "pending" : 답장이 없는 편지만 조회 (멘티가 보낸 편지 중 mentorLetter가 null인 경우)
   *    - "save"    : 저장된 편지만 조회 (멘티와 멘토 각각 menteeSaved 또는 mentorSaved 필터링)
   * 3. 조회된 데이터를 `ReceiveLetterDTO`로 변환하여 반환한다.
   * 4. 페이징 처리 후 최종 결과를 반환한다.
   *
   * @param user         현재 로그인한 사용자 (멘티 또는 멘토)
   * @param pageNumber   요청한 페이지 번호
   * @param letterListType 조회할 편지 유형 (all, pending, save)
   * @return 사용자 역할 및 요청 유형에 따른 편지 리스트 (페이지네이션 적용)
   */
  public PageResponse<ReceiveLetterDTO> getLetterList(UserEntity user, int pageNumber, String letterListType) {
    boolean isMentee = user.getRole().getRoleName().equals("MENTEE");
    BooleanBuilder builder = new BooleanBuilder();

    // 사용자 역할에 따라 mentee 또는 mentor 기준으로 필터링
    builder.and(isMentee ? letterStatus.mentee.eq(user) : letterStatus.mentor.eq(user));

    // letterListType에 따른 추가 필터 적용
    switch (letterListType) {
      case "all" :
        break; // 모든 편지를 조회 (추가 필터 없음)
      case "pending" :
        builder.and(letterStatus.mentorLetter.isNull()); // 멘토가 아직 답장하지 않은 편지 조회
        break;
      case "save" :
        builder.and(isMentee ? letterStatus.isMenteeSaved.isTrue() : letterStatus.isMentorSaved.isTrue()); // 저장된 편지 조회
        break;
    }

    // 조건을 적용하여 편지 목록 조회
    List<LetterStatusEntity> letterStatusList = jpaQueryFactory
        .selectFrom(letterStatus)
        .where(builder)
        .orderBy(letterStatus.createAt.desc()) // 최신순 정렬
        .fetch();

    // 사용자 역할에 따라 DTO 변환 처리
    List<ReceiveLetterDTO> receiveLetters = isMentee
        ? ReceiveLetterDTO.ofMentee(letterStatusList)
        : ReceiveLetterDTO.ofMentor(letterStatusList);

    // 페이징 처리 후 반환
    return PageResponse.pagination(receiveLetters, pageNumber);
  }

  @Transactional
  public void incrementCategory(Long letterCategorySeq, String categoryName) {
    NumberPath<Long> categoryField = getCategoryField(categoryName);

    jpaQueryFactory
        .update(throwLetterCategoryEntity)
        .set(categoryField, categoryField.add(1))
        .where(throwLetterCategoryEntity.throwLetterCategorySeq.eq(letterCategorySeq))
        .execute();
  }

  private NumberPath<Long> getCategoryField(String categoryName) {
    return switch (categoryName) {
      case "career" -> throwLetterCategoryEntity.career;
      case "mental" -> throwLetterCategoryEntity.mental;
      case "relationship" -> throwLetterCategoryEntity.relationship;
      case "love" -> throwLetterCategoryEntity.love;
      case "life" -> throwLetterCategoryEntity.life;
      case "finance" -> throwLetterCategoryEntity.finance;
      case "housing" -> throwLetterCategoryEntity.housing;
      case "other" -> throwLetterCategoryEntity.other;
      default -> throw new IllegalArgumentException("Invalid category name: " + categoryName);
    };
  }

}