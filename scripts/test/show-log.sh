#!/bin/bash
# 查看测试环境后端日志
# 用法: bash scripts/test/show-log.sh       (最近300行)
#       bash scripts/test/show-log.sh -f    (实时跟踪)
if [ "$1" = "-f" ]; then
    ssh root@120.27.148.45 "tail -f /web/logs/shaxian-server/catalina.out"
else
    ssh root@120.27.148.45 "tail -n 300 /web/logs/shaxian-server/catalina.out"
fi
