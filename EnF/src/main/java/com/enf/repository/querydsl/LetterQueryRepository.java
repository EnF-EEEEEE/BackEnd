package com.enf.repository.querydsl;

import com.enf.entity.LetterStatusEntity;
import com.enf.entity.QLetterStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * LetterQueryRepository
 *
 * 편지의 상태 정보를 조회하는 QueryDSL 기반의 JPA 레포지토리 클래스.
 * 멘티와 멘토가 받은 편지를 조회하는 기능을 담당하며, 페이징 처리 기능을 포함한다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class LetterQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  QLetterStatusEntity letterStatus = QLetterStatusEntity.letterStatusEntity;

  /**
   * 멘티가 보낸 편지 목록 조회 (페이징 지원)
   *
   * 1. 특정 멘티가 보낸 편지 목록을 가져온다.
   * 2. mentorLetter(멘토가 보낸 답장)가 null이면, 아직 답장이 없는 편지로 간주한다.
   * 3. mentorLetter가 존재하면, 해당 편지는 답장이 완료된 것으로 간주한다.
   * 4. 받은 데이터를 PageResponse 형태로 변환하여 반환한다.
   *
   * @param mentee     현재 로그인한 멘티 사용자
   * @param pageNumber 요청한 페이지 번호
   * @return 멘티가 보낸 편지 리스트 (페이지네이션 적용)
   */
  public PageResponse<ReceiveLetterDTO> getAllLetterListForMenTee(UserEntity mentee, int pageNumber) {

    // 멘티가 보낸 모든 편지 상태 조회
    List<LetterStatusEntity> letterStatusList = jpaQueryFactory
        .selectFrom(letterStatus)
        .where(letterStatus.mentee.eq(mentee))  // 특정 멘티가 보낸 편지만 조회
        .orderBy(letterStatus.createAt.desc())  // 날짜 순 정렬
        .fetch();

    // 편지 상태 데이터를 ReceiveLetterDTO로 변환
    List<ReceiveLetterDTO> receiveLetters = letterStatusList.stream()
        .map(letterStatus -> {
          if (letterStatus.getMentorLetter() == null) {
            // 답장이 없는 편지일 경우 익명 정보로 반환
            return new ReceiveLetterDTO(
                letterStatus.getMenteeLetter().getLetterSeq(),
                "익명새",
                "익명새",
                letterStatus.getMenteeLetter().getLetterTitle()
            );
          } else {
            // 답장이 있는 편지일 경우 멘토 정보를 포함하여 반환
            return new ReceiveLetterDTO(
                letterStatus.getMentorLetter().getLetterSeq(),
                letterStatus.getMentor().getBird().getBirdName(),
                letterStatus.getMentor().getNickname(),
                letterStatus.getMentorLetter().getLetterTitle()
            );
          }
        })
        .toList();

    // 페이징 처리 후 반환
    return PageResponse.pagination(receiveLetters, pageNumber);
  }

  /**
   * 멘토가 받은 편지 목록 조회 (페이징 지원)
   *
   * 1. 특정 멘토가 받은 편지 목록을 가져온다.
   * 2. 받은 편지는 menteeLetter로 구분되며, 답장을 보냈는지 여부는 별도 로직에서 확인 가능하다.
   * 3. 받은 데이터를 PageResponse 형태로 변환하여 반환한다.
   *
   * @param mentor     현재 로그인한 멘토 사용자
   * @param pageNumber 요청한 페이지 번호
   * @return 멘토가 받은 편지 리스트 (페이지네이션 적용)
   */
  public PageResponse<ReceiveLetterDTO> getAllLetterListForMentor(UserEntity mentor, int pageNumber) {

    // 멘토가 받은 모든 편지 상태 조회
    List<LetterStatusEntity> letterStatusList = jpaQueryFactory
        .selectFrom(letterStatus)
        .where(letterStatus.mentor.eq(mentor))  // 특정 멘토가 받은 편지만 조회
        .orderBy(letterStatus.createAt.desc())   // 날짜 순 정렬
        .fetch();

    // 편지 상태 데이터를 ReceiveLetterDTO로 변환
    List<ReceiveLetterDTO> receiveLetters = letterStatusList.stream()
        .map(letterStatus ->
            new ReceiveLetterDTO(
                letterStatus.getMenteeLetter().getLetterSeq(),
                letterStatus.getMentee().getBird().getBirdName(),
                letterStatus.getMentee().getNickname(),
                letterStatus.getMenteeLetter().getLetterTitle()
            )
        )
        .toList();

    // 페이징 처리 후 반환
    return PageResponse.pagination(receiveLetters, pageNumber);
  }

  /**
   * 미응답(멘토가 아직 답장하지 않은) 편지 목록을 조회하는 기능 (페이징 지원)
   * 1. 특정 멘티가 보낸 편지 중에서 아직 멘토가 답장하지 않은 편지를 조회
   * 2. 최신순으로 정렬하여 가져옴
   * 3. 조회된 데이터를 DTO로 변환하여 리스트로 생성
   * 4. 페이징 처리 후 결과 반환
   *
   * @param mentee     조회할 멘티 (편지를 보낸 사용자)
   * @param pageNumber 요청한 페이지 번호
   * @return 미응답 편지 리스트 (페이지네이션 적용)
   */
  public PageResponse<ReceiveLetterDTO> getPendingLetterList(UserEntity mentee, int pageNumber) {
    // 멘토가 아직 답장하지 않은 편지 상태 조회
    List<LetterStatusEntity> letterStatusList = jpaQueryFactory
        .selectFrom(letterStatus)
        .where(
            letterStatus.mentee.eq(mentee), // 특정 멘티가 보낸 편지
            letterStatus.mentorLetter.isNull() // 답장이 없는 상태만 조회
        )
        .orderBy(letterStatus.createAt.desc()) // 최신순 정렬
        .fetch();

    // 조회된 편지 상태 데이터를 ReceiveLetterDTO로 변환
    List<ReceiveLetterDTO> receiveLetters = letterStatusList.stream()
        .map(letterStatus ->
            new ReceiveLetterDTO(
                letterStatus.getMenteeLetter().getLetterSeq(),
                "익명새", // 답장이 없으므로 익명으로 표시
                "익명새",
                letterStatus.getMenteeLetter().getLetterTitle()
            )
        )
        .toList();

    // 페이징 처리 후 반환
    return PageResponse.pagination(receiveLetters, pageNumber);
  }
}