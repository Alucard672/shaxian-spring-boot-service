#!/bin/bash
# 查看生产环境后端日志
# 用法: bash scripts/prod/show-log.sh       (最近300行)
#       bash scripts/prod/show-log.sh -f    (实时跟踪)
if [ "$1" = "-f" ]; then
    ssh root@112.124.109.7 "tail -f /web/logs/shaxian-server/catalina.out"
else
    ssh root@112.124.109.7 "tail -n 300 /web/logs/shaxian-server/catalina.out"
fi
