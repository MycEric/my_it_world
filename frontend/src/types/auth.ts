/**
 * 统一 API 响应体类型（与后端 Result 对应）
 */
export interface ApiResult<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

/**
 * 登录/注册返回的 Token 信息
 */
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userId: number;
  username: string;
  roles: string[];
}

/**
 * 当前登录用户信息
 */
export interface UserInfo {
  userId: number;
  username: string;
  email?: string;
  roles: string[];
}

/**
 * 登录请求参数
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 注册请求参数
 */
export interface RegisterRequest {
  username: string;
  password: string;
  email?: string;
}
