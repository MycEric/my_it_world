"""Pydantic 请求/响应模型。"""

from typing import Any, List, Optional

from pydantic import BaseModel, Field


class ChatMessage(BaseModel):
    """单条对话消息。"""

    id: Optional[int] = None
    role: str = Field(description="user 或 assistant")
    content: str
    created_at: Optional[str] = None


class ChatRequest(BaseModel):
    """聊天请求：仅 session_id + 当前消息，历史由服务端加载。"""

    message: str = Field(..., min_length=1, max_length=4000, description="用户问题")
    session_id: Optional[str] = Field(default=None, description="会话 ID，可选")


class ChatResponseData(BaseModel):
    """聊天业务数据。"""

    content: str
    session_id: str
    model: str
    message_id: Optional[int] = None


class SessionSummary(BaseModel):
    """会话列表项。"""

    session_id: str
    title: Optional[str] = None
    model: str
    updated_at: str
    created_at: str


class SessionListData(BaseModel):
    """会话分页列表。"""

    list: List[SessionSummary]
    total: int
    page: int
    size: int


class SessionMessagesData(BaseModel):
    """某会话的全部消息。"""

    session_id: str
    title: Optional[str] = None
    messages: List[ChatMessage]


class UpdateSessionRequest(BaseModel):
    """修改会话标题。"""

    title: str = Field(..., min_length=1, max_length=200)


class Result(BaseModel):
    """与 Java 微服务统一的响应格式。"""

    code: int = 200
    message: str = "success"
    data: Optional[Any] = None
    timestamp: int = 0
