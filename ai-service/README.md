# AI Service 启动说明

## 1. 配置

复制 `.env.example` 为 `.env`，填入通义千问 Key 与 MySQL/Redis（与 Java 微服务一致）：

```bash
cd ai-service
copy .env.example .env
```

## 2. 数据库

执行会话表迁移：

```bash
mysql -u root -p < ../deploy/sql/ai_chat_migration.sql
```

## 3. 安装依赖并启动

```bash
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
python run.py
```

服务地址：http://localhost:8090

## 4. API 一览（一期：会话历史）

| 接口 | 说明 | 鉴权 |
|------|------|------|
| `POST /api/ai/chat` | 发送消息（仅 session_id + message） | 公开 |
| `GET /api/ai/sessions` | 会话列表 | 登录 |
| `GET /api/ai/sessions/{id}/messages` | 会话消息 | 公开（持 session_id） |
| `PUT /api/ai/sessions/{id}` | 改标题 | 登录 |
| `DELETE /api/ai/sessions/{id}` | 删会话 | 登录 |

经 Gateway：http://localhost:8080/api/ai/...

前端页面：http://localhost:5173/assistant
