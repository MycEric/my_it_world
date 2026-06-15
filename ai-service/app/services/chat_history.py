"""
LangChain MessageHistory：MySQL + Redis 双层存储。
给 LLM 的上下文做滑动窗口裁剪，完整历史仍保存在 MySQL。
"""

from typing import List, Optional

from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.messages import AIMessage, BaseMessage, HumanMessage, SystemMessage

from app.core.config import settings
from app.db.session import SessionLocal
from app.services.message_store import ChatMessageStore
from app.services.session_service import ChatSessionService


def _to_base_message(item: dict) -> BaseMessage:
    role = item["role"]
    content = item["content"]
    if role == "user":
        return HumanMessage(content=content)
    if role == "assistant":
        return AIMessage(content=content)
    if role == "system":
        return SystemMessage(content=content)
    return HumanMessage(content=content)


def _window_messages(messages: List[dict]) -> List[dict]:
    window = settings.history_window
    if window <= 0 or len(messages) <= window:
        return messages
    return messages[-window:]


class MySQLRedisChatHistory(BaseChatMessageHistory):
    def __init__(self, session_id: str) -> None:
        self.session_id = session_id

    @property
    def messages(self) -> List[BaseMessage]:
        return self.get_messages()

    def get_messages(self) -> List[BaseMessage]:
        db = SessionLocal()
        try:
            store = ChatMessageStore(db)
            raw = store.list_messages(self.session_id)
            windowed = _window_messages(raw)
            return [_to_base_message(m) for m in windowed]
        finally:
            db.close()

    def add_message(self, message: BaseMessage) -> None:
        db = SessionLocal()
        try:
            session_svc = ChatSessionService(db)
            session = session_svc.get_by_session_id(self.session_id)
            store = ChatMessageStore(db)

            if isinstance(message, HumanMessage):
                role = "user"
            elif isinstance(message, AIMessage):
                role = "assistant"
            elif isinstance(message, SystemMessage):
                role = "system"
            else:
                role = "user"

            content = message.content if isinstance(message.content, str) else str(message.content)
            store.append_message(self.session_id, role, content, session)
            session_svc.touch_session(self.session_id)
        finally:
            db.close()

    def clear(self) -> None:
        db = SessionLocal()
        try:
            store = ChatMessageStore(db)
            store.clear_cache(self.session_id)
        finally:
            db.close()


_history_instances: dict[str, MySQLRedisChatHistory] = {}


def get_session_history(session_id: str) -> BaseChatMessageHistory:
    if session_id not in _history_instances:
        _history_instances[session_id] = MySQLRedisChatHistory(session_id)
    return _history_instances[session_id]
