#!/bin/bash

# 拉取最新代码
echo "正在拉取最新代码..."
git pull

# 设置 Java 17 环境
echo "正在设置 Java 17 环境..."
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 编译打包
echo "正在编译打包..."
mvn clean package

# 检查编译是否成功
if [ $? -ne 0 ]; then
    echo "编译失败，请检查错误信息"
    exit 1
fi

# 进入 target 目录
cd target

# 运行应用
echo "正在启动应用..."
if [ -n "$1" ]; then
    echo "使用配置文件: application-$1.yml"
    java -jar shaxian-server.jar --spring.profiles.active=$1
else
    java -jar shaxian-server.jar
fi
