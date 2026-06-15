import request from '@/utils/request';
import { ApiResult } from '@/types/auth';
import {
  ChatRequest,
  ChatResponseData,
  SessionListData,
  SessionMessagesData,
  SessionSummary,
} from '@/types/ai';

/**
 * AI 对话（同步）
 * 经 Gateway 转发到 ai-service，历史由服务端按 session_id 加载
 */
export async function sendChatMessage(payload: ChatRequest): Promise<ChatResponseData> {
  const res = await request.post<ApiResult<ChatResponseData>>('/api/ai/chat', payload, {
    timeout: 60000,
  });
  return res.data.data;
}

/** 我的会话列表（需登录） */
export async function listChatSessions(page = 1, size = 20): Promise<SessionListData> {
  const res = await request.get<ApiResult<SessionListData>>('/api/ai/sessions', {
    params: { page, size },
  });
  return res.data.data;
}

/** 获取某会话全部消息 */
export async function getSessionMessages(sessionId: string): Promise<SessionMessagesData> {
  const res = await request.get<ApiResult<SessionMessagesData>>(
    `/api/ai/sessions/${sessionId}/messages`
  );
  return res.data.data;
}

/** 修改会话标题 */
export async function updateChatSession(
  sessionId: string,
  title: string
): Promise<SessionSummary> {
  const res = await request.put<ApiResult<SessionSummary>>(`/api/ai/sessions/${sessionId}`, {
    title,
  });
  return res.data.data;
}

/** 删除会话 */
export async function deleteChatSession(sessionId: string): Promise<void> {
  await request.delete(`/api/ai/sessions/${sessionId}`);
}
