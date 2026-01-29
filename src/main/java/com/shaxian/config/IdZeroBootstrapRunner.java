package com.shaxian.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 服务启动时为被外键引用且业务上使用 id=0 表示“无”的表插入 id=0 占位行，
 * 避免 NOT NULL + 外键约束下无法写入 0 的问题，同时不采用 NULL（节省索引空间）。
 * 插入顺序按依赖：products → colors → batches。
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class IdZeroBootstrapRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureIdZeroRows();
        } catch (Exception e) {
            log.warn("IdZero 占位数据初始化失败（若表结构或租户未迁移可忽略）: {}", e.getMessage());
        }
    }

    private void ensureIdZeroRows() {
        String dbProduct = getDatabaseProductName();
        if (!dbProduct.toLowerCase().contains("mysql")) {
            log.debug("当前数据库非 MySQL，跳过 id=0 占位插入");
            return;
        }

        // MySQL 下允许显式插入 id=0（否则 0 会被当作“取下一个自增值”）
        jdbcTemplate.execute("SET SESSION sql_mode = CONCAT(IFNULL(@@sql_mode, ''), ',NO_AUTO_VALUE_ON_ZERO')");

        // 依赖顺序：products → colors → batches
        insertProductIdZero();
        insertColorIdZero();
        insertBatchIdZero();
    }

    private String getDatabaseProductName() {
        try (Connection c = dataSource.getConnection()) {
            return c.getMetaData().getDatabaseProductName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void insertProductIdZero() {
        // tenant_id 由多租户迁移添加，若未迁移则无此列
        try {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO products (id, tenant_id, name, code, unit, type) VALUES (0, 1, '_NONE_', '_NONE_0', 'kg', 'RAW_MATERIAL')"
            );
        } catch (Exception e) {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO products (id, name, code, unit, type) VALUES (0, '_NONE_', '_NONE_0', 'kg', 'RAW_MATERIAL')"
            );
        }
    }

    private void insertColorIdZero() {
        try {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO colors (id, tenant_id, product_id, code, name, status) VALUES (0, 1, 0, '_NONE_0', '_NONE_', 'ON_SALE')"
            );
        } catch (Exception e) {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO colors (id, product_id, code, name, status) VALUES (0, 0, '_NONE_0', '_NONE_', 'ON_SALE')"
            );
        }
    }

    private void insertBatchIdZero() {
        try {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO batches (id, tenant_id, color_id, code, stock_quantity, initial_quantity) VALUES (0, 1, 0, '_NONE_0', 0, 0)"
            );
        } catch (Exception e) {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO batches (id, color_id, code, stock_quantity, initial_quantity) VALUES (0, 0, '_NONE_0', 0, 0)"
            );
        }
    }
}
