#!/bin/bash

ip=120.27.148.45
user=root
deploy_dir=/web/deploy/shaxian-erp
app_jar=shaxian-server.jar
app_log=/web/logs/shaxian-server/catalina.out
local_jar=target/shaxian-server.jar

echo "正在设置 Java 17 环境..."
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 编译本地代码为jar
echo "正在编译打包..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "编译失败，部署终止"
    exit 1
fi

if [ ! -f "$local_jar" ]; then
    echo "错误: 找不到编译后的jar文件 $local_jar"
    exit 1
fi

echo "编译成功"

# 判断服务器上的部署目录是否有jar文件
echo "检查服务器上的部署目录..."
remote_jar_path="$deploy_dir/$app_jar"
jar_exists=$(ssh $user@$ip "test -f $remote_jar_path && echo 'yes' || echo 'no'")

# 如有则备份jar文件
if [ "$jar_exists" = "yes" ]; then
    echo "发现现有jar文件，正在备份..."
    backup_name="${app_jar}.backup.$(date +%Y%m%d_%H%M%S)"
    ssh $user@$ip "cp $remote_jar_path $deploy_dir/$backup_name"
    if [ $? -eq 0 ]; then
        echo "备份成功: $backup_name"
    else
        echo "警告: 备份失败，但继续部署"
    fi
fi

# 上传本地编译的jar文件
echo "正在上传jar文件..."
rsync -avz --progress $local_jar $user@$ip:$remote_jar_path

if [ $? -ne 0 ]; then
    echo "上传失败，部署终止"
    exit 1
fi

echo "上传成功"

# 备份日志文件
backup_log_path="$deploy_dir/catalina.out.backup.$(date +%Y%m%d_%H%M%S)"
ssh $user@$ip "cp $app_log $backup_log_path"
if [ $? -eq 0 ]; then
    echo "日志备份成功: $backup_log_path"
else
    echo "警告: 日志备份失败，但继续部署"
fi

# 重启应用
echo "正在重启应用..."
# 先停止应用
ssh $user@$ip "cd $deploy_dir && pkill -f $app_jar || true"
# 再启动应用
ssh $user@$ip "cd $deploy_dir && nohup java -jar $app_jar --spring.profiles.active=prod > $app_log 2>&1 &"

if [ $? -eq 0 ]; then
    echo "应用重启成功"
    echo "部署完成"
else
    echo "警告: 应用重启可能失败，请手动检查"
    exit 1
fi
