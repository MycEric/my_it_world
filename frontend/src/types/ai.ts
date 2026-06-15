/** AI 聊天消息 */
export interface ChatMessage {
  id?: number;
  role: 'user' | 'assistant';
  content: string;
  created_at?: string;
}

/** AI 聊天请求 */
export interface ChatRequest {
  message: string;
  session_id?: string;
}

/** AI 聊天响应数据 */
export interface ChatResponseData {
  content: string;
  session_id: string;
  model: string;
  message_id?: number;
}

/** 会话列表项 */
export interface SessionSummary {
  session_id: string;
  title?: string;
  model: string;
  updated_at: string;
  created_at: string;
}

/** 会话分页列表 */
export interface SessionListData {
  list: SessionSummary[];
  total: number;
  page: number;
  size: number;
}

/** 会话消息列表 */
export interface SessionMessagesData {
  session_id: string;
  title?: string;
  messages: ChatMessage[];
}
