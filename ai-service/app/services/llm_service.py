"""
通义千问 LLM 封装
使用 LangChain ChatTongyi + RunnableWithMessageHistory 管理多轮对话。
"""

import os
from typing import Optional

from langchain_community.chat_models.tongyi import ChatTongyi
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.runnables.history import RunnableWithMessageHistory

from app.core.config import settings
from app.services.chat_history import get_session_history

_chain_with_history: Optional[RunnableWithMessageHistory] = None


def _build_chain() -> RunnableWithMessageHistory:
    if not settings.dashscope_api_key:
        raise ValueError(
            "未配置 DASHSCOPE_API_KEY，请在 ai-service/.env 中设置通义千问 API Key"
        )
    os.environ["DASHSCOPE_API_KEY"] = settings.dashscope_api_key

    llm = ChatTongyi(model=settings.tongyi_model)
    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", settings.system_prompt),
            MessagesPlaceholder(variable_name="history"),
            ("human", "{input}"),
        ]
    )
    chain = prompt | llm
    return RunnableWithMessageHistory(
        chain,
        get_session_history,
        input_messages_key="input",
        history_messages_key="history",
    )


def get_chat_chain() -> RunnableWithMessageHistory:
    global _chain_with_history
    if _chain_with_history is None:
        _chain_with_history = _build_chain()
    return _chain_with_history


def invoke_chat(session_id: str, message: str) -> str:
    """调用大模型并自动读写会话历史。"""
    chain = get_chat_chain()
    response = chain.invoke(
        {"input": message},
        config={"configurable": {"session_id": session_id}},
    )
    content = response.content if hasattr(response, "content") else str(response)
    return content.strip()
