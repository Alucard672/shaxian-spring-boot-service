package com.shaxian.crm.repository;

import com.shaxian.crm.entity.CrmUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrmUserInfoRepository extends JpaRepository<CrmUserInfo, Long> {
    Optional<CrmUserInfo> findByPhone(String phone);
    boolean existsByPhone(String phone);
}

