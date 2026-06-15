"""AI 对话与健康检查接口。"""

import time

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.core.config import settings
from app.core.deps import get_optional_user_id
from app.db.models import AiChatMessage
from app.db.session import get_db
from app.models.schemas import ChatRequest, ChatResponseData, Result
from app.services.llm_service import invoke_chat
from app.services.session_service import ChatSessionService

router = APIRouter(prefix="/api/ai", tags=["AI"])


@router.get("/health")
def health() -> dict:
    """健康检查（无需 API Key）。"""
    return {
        "status": "ok",
        "service": "ai-service",
        "model": settings.tongyi_model,
        "api_key_configured": bool(settings.dashscope_api_key),
    }


@router.post("/chat", response_model=Result)
def chat(
    request: ChatRequest,
    db: Session = Depends(get_db),
    user_id: int | None = Depends(get_optional_user_id),
) -> Result:
    """
    对话接口（同步）
    服务端根据 session_id 加载历史，前端不再传递 history。
    """
    session_svc = ChatSessionService(db)
    try:
        session = session_svc.ensure_session(
            session_id=request.session_id,
            user_id=user_id,
            first_message=request.message,
        )
        content = invoke_chat(session.session_id, request.message)

        last_msg = (
            db.query(AiChatMessage)
            .filter(
                AiChatMessage.session_id == session.session_id,
                AiChatMessage.role == "assistant",
            )
            .order_by(AiChatMessage.id.desc())
            .first()
        )

        return Result(
            code=200,
            message="success",
            data=ChatResponseData(
                content=content,
                session_id=session.session_id,
                model=settings.tongyi_model,
                message_id=last_msg.id if last_msg else None,
            ),
            timestamp=int(time.time() * 1000),
        )
    except PermissionError as e:
        raise HTTPException(status_code=403, detail=str(e)) from e
    except ValueError as e:
        raise HTTPException(status_code=500, detail=str(e)) from e
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI 调用失败: {e}") from e
