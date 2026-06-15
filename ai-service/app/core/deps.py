"""FastAPI 依赖：从 Gateway 转发的请求头解析用户信息。"""

from typing import Optional

from fastapi import Header


async def get_optional_user_id(
    x_user_id: Optional[str] = Header(default=None, alias="X-User-Id"),
) -> Optional[int]:
    """解析 Gateway JWT 过滤器注入的用户 ID；游客或未登录时为 None。"""
    if not x_user_id or not x_user_id.strip():
        return None
    try:
        return int(x_user_id)
    except ValueError:
        return None
