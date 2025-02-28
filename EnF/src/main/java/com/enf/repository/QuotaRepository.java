package com.enf.repository;

import com.enf.entity.QuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotaRepository extends JpaRepository<QuotaEntity, Long> {

}
