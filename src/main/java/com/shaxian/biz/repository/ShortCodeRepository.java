package com.shaxian.biz.repository;

import com.shaxian.biz.entity.ShortCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortCodeRepository extends JpaRepository<ShortCode, Long> {
    /**
     * 根据短码查找记录
     *
     * @param shortCode 短码
     * @return 短码记录
     */
    Optional<ShortCode> findByShortCode(String shortCode);

    /**
     * 检查短码是否存在
     *
     * @param shortCode 短码
     * @return 是否存在
     */
    boolean existsByShortCode(String shortCode);
}
