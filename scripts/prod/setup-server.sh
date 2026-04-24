#!/bin/bash
# ================================================================
# 生产服务器初始化脚本 - 阿里云 ECS (CentOS 7/8 / AlmaLinux 8/9)
# 用法: 本地执行 ssh root@<ECS_IP> 'bash -s' < setup_server.sh
# 或者: 上传到服务器后执行 bash setup_server.sh
# ================================================================

set -euo pipefail

echo "=========================================="
echo " 纱线ERP 生产服务器初始化"
echo "=========================================="

# -------- 配置区 (按需修改) --------
MYSQL_ROOT_PASSWORD="YourMySQLRootPass2025!"     # TODO: 改为强密码
MYSQL_APP_USER="shaxian_app"
MYSQL_APP_PASSWORD="YourAppDBPass2025!"           # TODO: 改为强密码
MYSQL_DATABASE="shaxian_erp"
REDIS_PASSWORD="YourRedisPass2025!"               # TODO: 改为强密码
DOMAIN="your-domain.com"                          # TODO: 替换为你的域名
# -----------------------------------

echo "[1/6] 更新系统包..."
yum update -y
yum install -y epel-release wget curl vim unzip git

# ---- JDK 17 ----
echo "[2/6] 安装 JDK 17..."
if ! java -version 2>&1 | grep -q '17'; then
    yum install -y java-17-openjdk java-17-openjdk-devel
fi
JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile.d/java.sh
source /etc/profile.d/java.sh
echo "Java 版本: $(java -version 2>&1 | head -1)"

# ---- MySQL 8.0 ----
echo "[3/6] 安装 MySQL 8.0..."
if ! command -v mysqld &> /dev/null; then
    # 阿里云 ECS 可能预装了 MariaDB，先移除
    yum remove -y mariadb* 2>/dev/null || true

    # 安装 MySQL 官方 repo
    if [ ! -f /etc/yum.repos.d/mysql-community.repo ]; then
        rpm -Uvh https://dev.mysql.com/get/mysql80-community-release-el7-11.noarch.rpm 2>/dev/null || \
        rpm -Uvh https://dev.mysql.com/get/mysql80-community-release-el8-9.noarch.rpm 2>/dev/null || \
        rpm -Uvh https://dev.mysql.com/get/mysql80-community-release-el9-5.noarch.rpm 2>/dev/null || true
    fi

    yum install -y mysql-community-server
fi

systemctl start mysqld
systemctl enable mysqld

# 获取临时密码并设置新密码
TEMP_PWD=$(grep 'temporary password' /var/log/mysqld.log | tail -1 | awk '{print $NF}')
if [ -n "$TEMP_PWD" ]; then
    echo "MySQL 临时密码: $TEMP_PWD"
    # 使用临时密码登录并修改root密码
    mysql --connect-expired-password -uroot -p"$TEMP_PWD" <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
FLUSH PRIVILEGES;
EOF
fi

# 创建数据库和应用用户
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<EOF
CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${MYSQL_APP_USER}'@'localhost' IDENTIFIED BY '${MYSQL_APP_PASSWORD}';
GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${MYSQL_APP_USER}'@'localhost';
FLUSH PRIVILEGES;
EOF
echo "MySQL 数据库 ${MYSQL_DATABASE} 和用户 ${MYSQL_APP_USER} 创建完成"

# ---- Redis ----
echo "[4/6] 安装 Redis..."
if ! command -v redis-server &> /dev/null; then
    yum install -y redis
fi

# 配置 Redis 密码和基本安全设置
cat > /etc/redis/redis-prod.conf <<EOF
bind 127.0.0.1
port 6379
daemonize yes
pidfile /var/run/redis_6379.pid
logfile /var/log/redis/redis.log
dir /var/lib/redis
requirepass ${REDIS_PASSWORD}
maxmemory 256mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
EOF

# 如果默认配置路径不同，也写入
if [ -f /etc/redis.conf ]; then
    sed -i "s/^# requirepass .*/requirepass ${REDIS_PASSWORD}/" /etc/redis.conf
    sed -i "s/^requirepass .*/requirepass ${REDIS_PASSWORD}/" /etc/redis.conf
    sed -i "s/^bind .*/bind 127.0.0.1/" /etc/redis.conf
fi

systemctl restart redis
systemctl enable redis
echo "Redis 已启动，密码已设置"

# ---- Nginx ----
echo "[5/6] 安装 Nginx..."
if ! command -v nginx &> /dev/null; then
    yum install -y nginx
fi

systemctl start nginx
systemctl enable nginx

# ---- 创建部署目录 ----
echo "[6/6] 创建部署目录..."
mkdir -p /web/deploy/shaxian-server
mkdir -p /web/deploy/shaxian-sales
mkdir -p /web/logs/shaxian-server
chown -R nginx:nginx /web/deploy/shaxian-sales

# ---- 防火墙配置 ----
echo "配置防火墙..."
if command -v firewall-cmd &> /dev/null; then
    firewall-cmd --permanent --add-service=http
    firewall-cmd --permanent --add-service=https
    firewall-cmd --reload
fi

echo ""
echo "=========================================="
echo " 初始化完成！"
echo "=========================================="
echo ""
echo "请记录以下信息："
echo "  MySQL root 密码:     ${MYSQL_ROOT_PASSWORD}"
echo "  MySQL 应用用户:      ${MYSQL_APP_USER}"
echo "  MySQL 应用密码:      ${MYSQL_APP_PASSWORD}"
echo "  Redis 密码:          ${REDIS_PASSWORD}"
echo "  数据库名:            ${MYSQL_DATABASE}"
echo ""
echo "下一步："
echo "  1. 导入数据库: mysql -u${MYSQL_APP_USER} -p ${MYSQL_DATABASE} < schema.sql"
echo "  2. 配置 Nginx: 参考 nginx-prod.conf"
echo "  3. 部署后端:   执行 deploy_prod.sh"
echo "  4. 部署前端:   执行 shaxian/scripts/deploy_prod.sh"
echo ""
echo "安全提示："
echo "  - 请在阿里云安全组中只开放 80/443 端口"
echo "  - 不要开放 3306(MySQL) 和 6379(Redis) 端口"
echo "  - 建议修改 SSH 默认端口并禁用密码登录"
