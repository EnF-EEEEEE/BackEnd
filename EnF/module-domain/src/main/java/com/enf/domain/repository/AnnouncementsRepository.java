package com.enf.domain.repository;

import com.enf.domain.entity.AnnouncementsEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementsRepository extends JpaRepository<AnnouncementsEntity, Long> {

 List<AnnouncementsEntity> findByCreatedAtBetween(LocalDateTime startOfWeek, LocalDateTime endOfWeek);
}
