package com.enf.domain.repository;

import com.enf.domain.entity.NotificationEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

  List<NotificationEntity> findAllByUserSeqOrderByCreatedAtDesc(Long userSeq);

  @Modifying
  @Transactional
  @Query("UPDATE notification n SET n.isRead = true where n.letterStatusSeq = :letterStatusSeq")
  void updateNotificationIsRead(Long letterStatusSeq);

  @Modifying
  @Transactional
  @Query("UPDATE notification n SET n.userSeq = :userSeq where n.letterStatusSeq = :letterStatusSeq")
  void changeMentor(Long letterStatusSeq, Long userSeq);
}
