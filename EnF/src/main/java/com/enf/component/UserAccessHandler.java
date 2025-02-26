package com.enf.component;

import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.type.FailedResultType;
import com.enf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAccessHandler {

    private final UserRepository userRepository;

    // userId 값과 일치하는 회원 정보 반환
    public UserEntity findByUserSeq(Long userId) {

        log.info("findByUserSeq -> userSeq : {}", userId);
        return userRepository.findByUserSeq(userId)
                .orElseThrow(() -> new GlobalException(FailedResultType.USER_NOT_FOUND));
    }
}
