package com.shaxian.biz.repository;

import com.shaxian.biz.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
}
