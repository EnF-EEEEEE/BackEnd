package com.enf.repository;

import com.enf.entity.NotificationEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

  List<NotificationEntity> findAllByUserSeqOrderByCreatedAtDesc(Long userSeq);
}
