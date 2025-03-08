package com.enf.repository;

import com.enf.entity.NotificationStatusEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationStatusRepository extends JpaRepository<NotificationStatusEntity, Long> {

  List<NotificationStatusEntity> findAllByUserSeq(Long userSeq);

  @Transactional
  void deleteAllByUserSeq(Long userSeq);
}
