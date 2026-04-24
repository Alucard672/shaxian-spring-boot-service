#!/bin/bash
# ================================================================
# 后端 → 生产环境 部署
# 用法: cd shaxian-spring-boot-service && bash scripts/prod/deploy.sh
# ================================================================

set -euo pipefail

# -------- 配置 --------
SERVER_IP="${PROD_IP:-112.124.109.7}"
SERVER_USER="${PROD_USER:-root}"
DEPLOY_DIR=/web/deploy/shaxian-server
APP_JAR=shaxian-server.jar
APP_LOG=/web/logs/shaxian-server/catalina.out
LOCAL_JAR=target/$APP_JAR
# ----------------------

# SSH 连接
SSH_KEY_PATH="${SSH_KEY_PATH:-$HOME/.ssh/id_ed25519}"
if [ -f "$SSH_KEY_PATH" ]; then
    SSH_OPTS="-i $SSH_KEY_PATH -o StrictHostKeyChecking=accept-new"
    echo "使用 SSH Key 连接"
else
    echo "未找到 SSH Key，将使用密码连接"
    echo "提示: 建议执行 ssh-copy-id ${SERVER_USER}@${SERVER_IP} 配置免密登录"
    SSH_OPTS="-o StrictHostKeyChecking=accept-new"
fi

echo "=========================================="
echo " 纱线ERP 后端 → 生产环境"
echo " 目标: ${SERVER_USER}@${SERVER_IP}"
echo "=========================================="

# [1/5] 编译
echo ""
echo "[1/5] 编译打包..."
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean package -DskipTests

if [ ! -f "$LOCAL_JAR" ]; then
    echo "错误: 找不到 $LOCAL_JAR"
    exit 1
fi
echo "编译成功: $(ls -lh $LOCAL_JAR | awk '{print $5}')"

# [2/5] 备份 JAR
echo ""
echo "[2/5] 备份现有版本..."
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REMOTE_JAR="$DEPLOY_DIR/$APP_JAR"

jar_exists=$(ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "test -f $REMOTE_JAR && echo 'yes' || echo 'no'")
if [ "$jar_exists" = "yes" ]; then
    ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "cp $REMOTE_JAR $DEPLOY_DIR/${APP_JAR}.backup.${TIMESTAMP}"
    echo "备份完成"
    ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "cd $DEPLOY_DIR && ls -t ${APP_JAR}.backup.* 2>/dev/null | tail -n +6 | xargs rm -f 2>/dev/null || true"
fi

# [3/5] 备份日志
echo ""
echo "[3/5] 备份日志..."
if ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "test -f $APP_LOG"; then
    ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "cp $APP_LOG ${APP_LOG}.backup.${TIMESTAMP}"
    echo "日志备份完成"
fi

# [4/5] 上传
echo ""
echo "[4/5] 上传 JAR..."
rsync -avz --progress -e "ssh $SSH_OPTS" "$LOCAL_JAR" $SERVER_USER@$SERVER_IP:$REMOTE_JAR

# [5/5] 重启 (prod profile)
echo ""
echo "[5/5] 启动应用 (prod profile)..."
ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "pkill -f $APP_JAR || true"
sleep 3
ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "cd $DEPLOY_DIR && nohup java -Xms512m -Xmx1024m -jar $APP_JAR --spring.profiles.active=prod >> $APP_LOG 2>&1 &"

sleep 8
IS_RUNNING=$(ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "pgrep -f $APP_JAR > /dev/null && echo 'yes' || echo 'no'")

if [ "$IS_RUNNING" = "yes" ]; then
    echo ""
    echo "=========================================="
    echo " 生产环境部署成功！"
    echo " 日志: ssh ${SERVER_USER}@${SERVER_IP} 'tail -f $APP_LOG'"
    echo "=========================================="
else
    echo ""
    echo "启动失败！最近日志:"
    ssh $SSH_OPTS $SERVER_USER@$SERVER_IP "tail -30 $APP_LOG"
    exit 1
fi
