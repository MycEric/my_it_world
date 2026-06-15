import request from '@/utils/request';
import { ApiResult, LoginRequest, RegisterRequest, TokenResponse, UserInfo } from '@/types/auth';

/**
 * 认证相关 API
 * <p>
 * 所有请求经 Vite 代理转发到 gateway-service (8080)，
 * 再由网关路由到 auth-service。
 * </p>
 */

/** 用户登录 */
export async function loginApi(data: LoginRequest): Promise<TokenResponse> {
  const res = await request.post<ApiResult<TokenResponse>>('/api/auth/login', data);
  return res.data.data;
}

/** 用户注册 */
export async function registerApi(data: RegisterRequest): Promise<TokenResponse> {
  const res = await request.post<ApiResult<TokenResponse>>('/api/auth/register', data);
  return res.data.data;
}

/** 刷新 Token */
export async function refreshTokenApi(refreshToken: string): Promise<TokenResponse> {
  const res = await request.post<ApiResult<TokenResponse>>('/api/auth/refresh', { refreshToken });
  return res.data.data;
}

/** 获取当前登录用户信息（需 Token） */
export async function getCurrentUserApi(): Promise<UserInfo> {
  const res = await request.get<ApiResult<UserInfo>>('/api/auth/me');
  return res.data.data;
}

/** 用户登出 */
export async function logoutApi(): Promise<void> {
  await request.post('/api/auth/logout');
}
