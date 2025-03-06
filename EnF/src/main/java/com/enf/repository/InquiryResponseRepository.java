package com.enf.repository;

import com.enf.entity.InquiryResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InquiryResponseRepository extends JpaRepository<InquiryResponseEntity, Long> {

}