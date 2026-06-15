"""AI 服务配置：从环境变量读取通义千问 API Key、MySQL、Redis 等。"""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """应用配置，支持 .env 文件与环境变量。"""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    # 通义千问 / DashScope API Key
    dashscope_api_key: str = ""

    # 模型：qwen-turbo / qwen-plus / qwen-max 等
    tongyi_model: str = "qwen-turbo"

    # 服务端口
    ai_service_port: int = 8090

    # 系统提示词（简单对话阶段）
    system_prompt: str = (
        "你是 My IT World 个人网站上的 AI 助手，擅长解答编程、云计算、Java、"
        "Spring、前端等技术问题。回答请简洁清晰，使用中文。"
    )

    # MySQL（与 Java 微服务共用 myit_world 库）
    mysql_host: str = "127.0.0.1"
    mysql_port: int = 3306
    mysql_user: str = "root"
    mysql_password: str = "demo1"
    mysql_database: str = "myit_world"

    # Redis
    redis_host: str = "127.0.0.1"
    redis_port: int = 6379
    redis_password: str = ""
    redis_database: int = 0

    # 对话历史：给 LLM 的滑动窗口（消息条数，user+assistant 合计）
    history_window: int = 20

    # Redis 缓存 TTL（秒）：游客会话 7 天
    guest_session_ttl_seconds: int = 7 * 24 * 3600

    # 登录用户会话 Redis TTL（秒），0 表示不过期
    user_session_ttl_seconds: int = 0

    @property
    def mysql_url(self) -> str:
        return (
            f"mysql+pymysql://{self.mysql_user}:{self.mysql_password}"
            f"@{self.mysql_host}:{self.mysql_port}/{self.mysql_database}"
            "?charset=utf8mb4"
        )


settings = Settings()
