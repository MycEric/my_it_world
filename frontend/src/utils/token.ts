/**
 * Token 本地存储工具
 * <p>
 * - Access Token 存 sessionStorage：关闭浏览器标签后失效，降低 XSS 持久化风险
 * - Refresh Token 存 localStorage：用于跨会话静默刷新（阶段一基础实现）
 * </p>
 */

const ACCESS_TOKEN_KEY = 'myit_access_token';
const REFRESH_TOKEN_KEY = 'myit_refresh_token';

/** 保存 Token 对 */
export function saveTokens(accessToken: string, refreshToken: string): void {
  sessionStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

/** 获取 Access Token */
export function getAccessToken(): string | null {
  return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}

/** 获取 Refresh Token */
export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

/** 清除所有 Token（登出时调用） */
export function clearTokens(): void {
  sessionStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
}

/** 是否已登录（本地存在 Access Token） */
export function isLoggedIn(): boolean {
  return !!getAccessToken();
}
