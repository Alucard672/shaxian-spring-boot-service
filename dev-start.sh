#!/bin/bash

# 开发环境启动脚本
# 使用 dev profile，自动维护数据库表结构

echo "=== 开发环境启动 ==="
echo "配置说明："
echo "  - Profile: dev"
echo "  - 数据库表结构：自动更新（根据 JPA 实体类）"
echo ""

# 设置 Java 17 环境
export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null)

if [ -z "$JAVA_HOME" ]; then
    echo "警告: 未找到 Java 17，使用系统默认 Java"
fi

# 检查是否需要编译
if [ "$1" = "--build" ] || [ ! -f "target/shaxian-server.jar" ]; then
    echo "正在编译打包..."
    mvn clean package -DskipTests
    
    if [ $? -ne 0 ]; then
        echo "编译失败，请检查错误信息"
        exit 1
    fi
fi

# 启动应用，使用 dev profile
echo "正在启动应用（开发环境）..."
echo "使用配置文件: application-dev.yml"
echo ""

cd target
java -jar shaxian-server.jar --spring.profiles.active=dev
