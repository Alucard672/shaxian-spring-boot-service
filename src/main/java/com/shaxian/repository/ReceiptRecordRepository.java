package com.shaxian.repository;

import com.shaxian.entity.ReceiptRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRecordRepository extends JpaRepository<ReceiptRecord, Long> {
    List<ReceiptRecord> findByAccountReceivableIdOrderByReceiptDateDesc(Long accountReceivableId);
}

