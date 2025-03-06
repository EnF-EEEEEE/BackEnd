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

@Repository
@RequiredArgsConstructor
public class LetterQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QLetterEntity letter = QLetterEntity.letterEntity;
    private final QLetterStatusEntity letterStatus = QLetterStatusEntity.letterStatusEntity;


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
}