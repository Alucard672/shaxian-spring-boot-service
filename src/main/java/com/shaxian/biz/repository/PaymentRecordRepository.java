package com.shaxian.biz.repository;

import com.shaxian.biz.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findByAccountPayableIdOrderByPaymentDateDesc(Long accountPayableId);
}

