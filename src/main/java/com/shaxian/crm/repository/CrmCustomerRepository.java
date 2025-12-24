package com.shaxian.crm.repository;

import com.shaxian.crm.entity.CrmCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrmCustomerRepository extends JpaRepository<CrmCustomer, Long> {
    Optional<CrmCustomer> findByPhone(String phone);
    boolean existsByPhone(String phone);
}

