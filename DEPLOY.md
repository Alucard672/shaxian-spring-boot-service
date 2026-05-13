# 纱线ERP 部署指南

## 环境总览

| 项目 | 测试环境 | 生产环境 |
|------|---------|---------|
| **服务器 IP** | 120.27.148.45 | 112.124.109.7 |
| **域名** | t.jiyizhiyun.com | jiyizhiyun.com |
| **协议** | HTTP | HTTPS (Let's Encrypt) |
| **前端地址** | http://t.jiyizhiyun.com | https://jiyizhiyun.com |
| **管理端地址** | — | https://admin.jiyizhiyun.com |
| **API 地址** | http://t.jiyizhiyun.com/biz/api | https://jiyizhiyun.com/biz/api |
| **后端 Profile** | (默认/dev) | prod |
| **数据库** | MySQL (本地) | MySQL 8.4 (本地) |
| **Redis** | Redis (本地) | Redis 7.2 (本地) |
| **操作系统** | — | Rocky Linux 10 |

---

## 项目结构

```
shaxian/                          # 前端 (React + Vite)
├── scripts/
│   ├── test/
│   │   └── deploy.sh             # 前端 → 测试环境
│   └── prod/
│       └── deploy.sh             # 前端 → 生产环境
├── .env.production               # 生产环境变量 (API地址)
├── .env.example                  # 环境变量示例
└── src/

shaxian-admin-web/                # 管理端前端 (Umi Max + Ant Design Pro)
├── scripts/
│   ├── test/
│   │   └── deploy.sh             # 管理端 → 测试环境
│   └── prod/
│       ├── deploy.sh             # 管理端 → 生产环境
│       └── nginx.conf            # Nginx 配置参考
└── src/

shaxian-spring-boot-service/      # 后端 (Spring Boot)
├── scripts/
│   ├── build.sh                  # 本地编译
│   ├── test/
│   │   ├── deploy.sh             # 后端 → 测试环境
│   │   ├── login.sh              # SSH 登录测试服务器
│   │   └── show-log.sh           # 查看测试日志
│   └── prod/
│       ├── deploy.sh             # 后端 → 生产环境
│       ├── login.sh              # SSH 登录生产服务器
│       ├── show-log.sh           # 查看生产日志 (-f 实时跟踪)
│       ├── nginx.conf            # Nginx 配置参考
│       └── setup-server.sh       # 服务器初始化 (仅首次)
├── src/main/resources/
│   ├── application.yml           # 通用配置
│   ├── application-dev.yml       # 开发环境
│   ├── application-qiuxs.yml     # 内网测试环境
│   └── application-prod.yml      # 生产环境
└── start.sh                      # 本地开发启动
```

---

## 日常发布流程

> 各脚本在对应工程目录下执行

### 发布后端

```bash
cd shaxian-spring-boot-service

bash scripts/test/deploy.sh       # → 测试环境
bash scripts/prod/deploy.sh       # → 生产环境
```

### 发布前端（客户端）

```bash
cd shaxian

bash scripts/test/deploy.sh       # → 测试环境
bash scripts/prod/deploy.sh       # → 生产环境
```

### 发布管理端前端

```bash
cd shaxian-admin-web

bash scripts/test/deploy.sh       # → 测试环境
bash scripts/prod/deploy.sh       # → 生产环境
```

### 建议流程

1. 代码修改完成
2. 先部署到 **测试环境** 验证功能
3. 测试通过后，再部署到 **生产环境**

> ⚠️ **切勿**跳过测试环境直接部署生产！

---

## 部署脚本做了什么

### 后端部署脚本 (deploy_test.sh / deploy_prod.sh)

1. 设置 Java 17 环境
2. `mvn clean package -DskipTests` 编译打包
3. 备份服务器上现有的 JAR 文件（保留最近5个备份）
4. 备份日志文件
5. rsync 上传新 JAR 到服务器
6. 停止旧进程 → 启动新进程
7. 检查启动是否成功

### 前端部署脚本 (deploy_test.sh / deploy_prod.sh)

1. `npm install` / `npm ci` 安装依赖
2. `npm run build` 构建（自动读取对应的 .env 文件）
3. rsync 增量上传 dist/ 到服务器
4. 设置 nginx 文件权限

---

## 服务器上的关键路径

| 内容 | 路径 |
|------|------|
| 后端 JAR | `/web/deploy/shaxian-server/shaxian-server.jar` |
| 前端文件（客户端） | `/web/deploy/shaxian-sales/` |
| 前端文件（管理端） | `/web/deploy/shaxian-admin/` |
| 后端日志 | `/web/logs/shaxian-server/catalina.out` |
| Nginx 配置（客户端） | `/etc/nginx/conf.d/shaxian-prod.conf` |
| Nginx 配置（管理端） | `/etc/nginx/conf.d/shaxian-admin.conf` |
| Nginx 日志 | `/var/log/nginx/shaxian-prod-*.log` |
| SSL 证书（客户端） | `/etc/letsencrypt/live/jiyizhiyun.com/` |
| SSL 证书（管理端） | `/etc/letsencrypt/live/admin.jiyizhiyun.com/` |
| Redis 配置 | `/etc/redis/redis.conf` |

---

## SSH 连接

部署脚本支持两种连接方式：

1. **SSH Key（推荐）**：如果 `~/.ssh/id_ed25519` 存在则自动使用
2. **密码**：没有 Key 时会提示输入密码

配置免密登录：
```bash
# 生成 Key（如果没有）
ssh-keygen -t ed25519 -C "shaxian-deploy"

# 复制到测试服务器
ssh-copy-id root@120.27.148.45

# 复制到生产服务器
ssh-copy-id root@112.124.109.7
```

---

## 常用运维命令

```bash
# ===== 测试环境 (在 shaxian-spring-boot-service/ 下) =====
bash scripts/test/login.sh             # 登录测试服务器
bash scripts/test/show-log.sh           # 查看测试日志
bash scripts/test/show-log.sh -f        # 实时跟踪测试日志

# ===== 生产环境 (在 shaxian-spring-boot-service/ 下) =====
bash scripts/prod/login.sh              # 登录生产服务器
bash scripts/prod/show-log.sh           # 查看生产日志 (最近300行)
bash scripts/prod/show-log.sh -f        # 实时跟踪生产日志
```

### 其他运维操作 (生产环境 112.124.109.7)

```bash
# 手动重启后端
ssh root@112.124.109.7 'pkill -f shaxian-server.jar; cd /web/deploy/shaxian-server && nohup java -Xms512m -Xmx1024m -jar shaxian-server.jar --spring.profiles.active=prod >> /web/logs/shaxian-server/catalina.out 2>&1 &'

# 查看 Nginx 状态
ssh root@112.124.109.7 'systemctl status nginx'

# 重载 Nginx 配置
ssh root@112.124.109.7 'nginx -t && systemctl reload nginx'

# 查看 SSL 证书到期时间
ssh root@112.124.109.7 'certbot certificates'

# 手动续期 SSL 证书（通常自动续期，无需手动）
ssh root@112.124.109.7 'certbot renew'
```

---

## 环境差异说明

### 后端配置差异 (application-*.yml)

| 配置项 | 测试/开发 | 生产 (prod) |
|--------|----------|-------------|
| `ddl-auto` | `update`（自动建表） | `none`（禁止） |
| `show-sql` | `true` | `false` |
| 日志级别 | `DEBUG` | `INFO` |
| 数据库连接池 | 默认 (10) | 最大 20 |
| Redis 连接池 | 默认 (8) | 最大 16 |
| 数据库用户 | root | shaxian_app |

### 前端环境变量

| 文件 | API 地址 |
|------|---------|
| `.env.example` (开发) | `http://t.jiyizhiyun.com/biz/api` |
| `.env.production` (生产) | `https://jiyizhiyun.com/biz/api` |

> 注意：客户端前端 API 地址中的路径前缀是 `/biz/api`，不是 `/api`。
> 管理端前端使用 `/api` 路径，由 Nginx 自动重写为 `/biz/api`。

---

## 注意事项

1. **生产环境禁止 ddl-auto: update**，表结构变更必须通过 SQL migration 文件手动执行
2. **数据库 migration SQL** 位于 `shaxian-spring-boot-service/database/` 目录
3. **SSL 证书** 由 certbot 自动续期（每90天），无需手动干预
4. **安全组** 只开放 80/443 端口，MySQL(3306) 和 Redis(6379) 不对外暴露
5. 前端构建时 Vite **自动读取** `.env.production`，无需手动指定
