package com.shaxian.biz.repository;

import com.shaxian.biz.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    List<Color> findByProductIdOrderByCode(Long productId);
}

