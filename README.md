# My IT World — 个人展示网站

基于 **React + Spring Cloud Alibaba** 的个人 IT 学习成果展示平台。

## 项目结构

```
my_it_world/
├── frontend/              # React 18 + TypeScript + Vite 前端
├── backend/
│   ├── common/            # 公共模块（Result、JWT、异常处理）
│   ├── auth-service/      # 认证服务（注册/登录/JWT）
│   ├── gateway-service/   # API 网关（路由/鉴权/CORS）
│   └── blog-service/      # 博客服务（Markdown 存储/发布）
├── deploy/                # Docker Compose + SQL 初始化
└── docs/                  # 设计文档
```

## 已完成功能

### 阶段一：认证骨架
- [x] auth-service、gateway-service、JWT 鉴权
- [x] React 登录/注册/路由守卫

### 博客模块（模式 A：MySQL 存 Markdown）
- [x] blog-service：CRUD、草稿/发布/下架、.md 导入
- [x] 前台：博客列表、详情、Markdown 渲染
- [x] Admin：在线编辑（分栏预览）、文章管理

> 若数据库已存在且无博客表，请执行 `deploy/sql/blog_migration.sql`

## 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Docker | 20+ |

## 快速启动

### 1. 启动基础设施

```bash
cd deploy
docker compose up -d
```

等待 MySQL、Redis、Nacos 健康检查通过（约 1–2 分钟）。

- Nacos 控制台：http://localhost:8848/nacos （默认无鉴权）
- MySQL：`root` / `root123456`，库名 `myit_world`

### 2. 启动后端

```bash
cd backend

# 编译全部模块
mvn clean install -DskipTests

# 终端 1：启动 auth-service
cd auth-service
mvn spring-boot:run

# 终端 2：启动 gateway-service
cd gateway-service
mvn spring-boot:run

# 终端 3：启动 blog-service
cd blog-service
mvn spring-boot:run
```

服务端口：

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway-service | 8080 | API 统一入口 |
| auth-service | 8081 | 认证服务 |
| blog-service | 8084 | 博客服务 |
| Knife4j | 8084/doc.html | blog-service 接口文档 |

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问：http://localhost:5173

### 4. 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | ADMIN + USER |

> 首次启动 auth-service 时会自动创建 admin 账号（`DataInitializer`）。

## API 示例

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 博客列表（公开）
curl http://localhost:8080/api/blogs?page=1&size=10

# 新建博客（需 Admin Token）
curl -X POST http://localhost:8080/api/blogs/admin \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"title":"测试","content":"# Hello\n\nMarkdown 正文","status":1}'

# 批量导入 CSDN 导出包（backend/output/index.json + articles/）
curl -X POST http://localhost:8080/api/blogs/admin/import/batch \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"skipExisting":true,"publish":true}'
```

## 架构说明

```
浏览器 (5173)
    ↓ /api 代理
Gateway (8080) ──→ auth-service (8081)
              └──→ blog-service (8084) ──→ MySQL (blog_article.content = Markdown)
```

## 后续阶段

详见 [docs/系统设计企划书.md](docs/系统设计企划书.md)：

- **阶段二**：content-service + 技能/项目展示
- **阶段三**：blog-service + CSDN 同步
- **阶段四**：file-service + 文件上传
- **阶段五**：RocketMQ、CI/CD、监控

## 许可证

MIT
