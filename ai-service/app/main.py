"""
My IT World AI Service
FastAPI 入口，端口默认 8090。
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.chat import router as chat_router
from app.api.sessions import router as sessions_router

app = FastAPI(
    title="My IT World AI Service",
    description="通义千问对话服务（会话历史一期）",
    version="1.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat_router)
app.include_router(sessions_router)


@app.get("/")
def root() -> dict:
    return {"service": "ai-service", "docs": "/docs"}
