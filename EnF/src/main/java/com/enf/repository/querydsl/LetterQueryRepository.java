package com.enf.repository.querydsl;

import com.enf.entity.LetterStatusEntity;
import com.enf.entity.QLetterStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Slf4j
@Repository
@RequiredArgsConstructor
public class LetterQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  QLetterStatusEntity letterStatus = QLetterStatusEntity.letterStatusEntity;

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

}