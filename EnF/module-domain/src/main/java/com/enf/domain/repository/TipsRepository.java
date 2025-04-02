package com.enf.domain.repository;

import com.enf.domain.entity.TipsEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipsRepository extends JpaRepository<TipsEntity, Long> {

  List<TipsEntity> findAllByType(String type);
}
