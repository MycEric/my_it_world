"""会话 CRUD 与权限校验。"""

import uuid
from datetime import datetime
from typing import List, Optional, Tuple

from sqlalchemy import desc, func
from sqlalchemy.orm import Session

from app.core.config import settings
from app.db.models import AiChatSession


def _truncate_title(text: str, max_len: int = 80) -> str:
    cleaned = text.strip().replace("\n", " ")
    if len(cleaned) <= max_len:
        return cleaned
    return cleaned[: max_len - 1] + "…"


class ChatSessionService:
    def __init__(self, db: Session) -> None:
        self._db = db

    def get_by_session_id(self, session_id: str) -> Optional[AiChatSession]:
        return (
            self._db.query(AiChatSession)
            .filter(
                AiChatSession.session_id == session_id,
                AiChatSession.deleted == 0,
            )
            .first()
        )

    def create_session(
        self,
        user_id: Optional[int] = None,
        title: Optional[str] = None,
    ) -> AiChatSession:
        session = AiChatSession(
            session_id=str(uuid.uuid4()),
            user_id=user_id,
            title=title,
            model=settings.tongyi_model,
        )
        self._db.add(session)
        self._db.commit()
        self._db.refresh(session)
        return session

    def ensure_session(
        self,
        session_id: Optional[str],
        user_id: Optional[int],
        first_message: Optional[str] = None,
    ) -> AiChatSession:
        """获取或创建会话；校验归属权。"""
        if session_id:
            existing = self.get_by_session_id(session_id)
            if existing:
                if not self._can_access(existing, user_id):
                    raise PermissionError("无权访问该会话")
                # 登录用户继续游客会话时，绑定归属
                if existing.user_id is None and user_id is not None:
                    existing.user_id = user_id
                    existing.updated_at = datetime.now()
                    self._db.commit()
                    self._db.refresh(existing)
                if first_message and not existing.title:
                    existing.title = _truncate_title(first_message)
                    existing.updated_at = datetime.now()
                    self._db.commit()
                    self._db.refresh(existing)
                return existing

        title = _truncate_title(first_message) if first_message else "新对话"
        return self.create_session(user_id=user_id, title=title)

    def list_sessions(
        self,
        user_id: int,
        page: int = 1,
        size: int = 20,
    ) -> Tuple[List[AiChatSession], int]:
        query = (
            self._db.query(AiChatSession)
            .filter(AiChatSession.user_id == user_id, AiChatSession.deleted == 0)
            .order_by(desc(AiChatSession.updated_at))
        )
        total = query.count()
        items = query.offset((page - 1) * size).limit(size).all()
        return items, total

    def update_title(
        self,
        session_id: str,
        user_id: int,
        title: str,
    ) -> AiChatSession:
        session = self.get_by_session_id(session_id)
        if not session:
            raise ValueError("会话不存在")
        if not self._can_access(session, user_id):
            raise PermissionError("无权修改该会话")
        session.title = title.strip()[:200]
        session.updated_at = datetime.now()
        self._db.commit()
        self._db.refresh(session)
        return session

    def delete_session(self, session_id: str, user_id: int) -> None:
        session = self.get_by_session_id(session_id)
        if not session:
            raise ValueError("会话不存在")
        if session.user_id != user_id:
            raise PermissionError("无权删除该会话")
        session.deleted = 1
        session.updated_at = datetime.now()
        self._db.commit()

    def touch_session(self, session_id: str) -> None:
        session = self.get_by_session_id(session_id)
        if session:
            session.updated_at = datetime.now()
            self._db.commit()

    def can_access(self, session: AiChatSession, user_id: Optional[int]) -> bool:
        """游客会话（user_id 为空）任何人持 session_id 可访问；登录会话仅本人。"""
        if session.user_id is None:
            return True
        return user_id is not None and session.user_id == user_id

    def _can_access(self, session: AiChatSession, user_id: Optional[int]) -> bool:
        return self.can_access(session, user_id)
