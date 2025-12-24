package com.shaxian.biz.repository;

import com.shaxian.biz.entity.ReceiptRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRecordRepository extends JpaRepository<ReceiptRecord, Long> {
    List<ReceiptRecord> findByAccountReceivableIdOrderByReceiptDateDesc(Long accountReceivableId);
}

