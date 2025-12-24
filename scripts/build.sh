#!/bin/bash

echo "正在设置 Java 17 环境..."
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

echo "正在编译打包..."
mvn clean package

echo "编译成功"