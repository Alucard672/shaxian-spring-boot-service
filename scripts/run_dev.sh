#!/bin/bash

echo "正在设置 Java 17 环境..."
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

echo "正在启动应用..."
mvn clean -DskipTests spring-boot:run --spring.profiles.active=qiuxs