import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ApiResult, TokenResponse } from '@/types/auth';
import { clearTokens, getAccessToken, getRefreshToken, saveTokens } from '@/utils/token';

/**
 * Axios 实例配置
 * <p>
 * - baseURL：开发环境为空，走 Vite 代理；生产环境配置为网关地址
 * - 请求拦截器：自动注入 Authorization 头
 * - 响应拦截器：统一处理业务 code；401 时尝试 Refresh Token
 * </p>
 */
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
});

/** 是否正在刷新 Token，防止并发请求重复刷新 */
let isRefreshing = false;
/** 刷新 Token 期间挂起的请求队列 */
let pendingQueue: Array<(token: string) => void> = [];

/**
 * 请求拦截：为每个请求附加 JWT
 */
request.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * 响应拦截：解析统一 Result 格式，处理 Token 过期自动刷新
 */
request.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResult;
    // 业务失败（HTTP 200 但 code != 200）
    if (result.code !== 200) {
      return Promise.reject(new Error(result.message || '请求失败'));
    }
    return response;
  },
  async (error: AxiosError<ApiResult>) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Token 过期：尝试用 Refresh Token 换新
    if (
      error.response?.data?.code === 40102 &&
      originalRequest &&
      !originalRequest._retry
    ) {
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        clearTokens();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      if (isRefreshing) {
        // 已有刷新进行中，当前请求排队等待
        return new Promise((resolve) => {
          pendingQueue.push((newToken: string) => {
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            resolve(request(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshRes = await axios.post<ApiResult<TokenResponse>>(
          `${import.meta.env.VITE_API_BASE_URL || ''}/api/auth/refresh`,
          { refreshToken }
        );
        const tokenData = refreshRes.data.data;
        saveTokens(tokenData.accessToken, tokenData.refreshToken);

        // 唤醒排队请求
        pendingQueue.forEach((cb) => cb(tokenData.accessToken));
        pendingQueue = [];

        originalRequest.headers.Authorization = `Bearer ${tokenData.accessToken}`;
        return request(originalRequest);
      } catch {
        clearTokens();
        window.location.href = '/login';
        return Promise.reject(error);
      } finally {
        isRefreshing = false;
      }
    }

    const message = error.response?.data?.message || error.message || '网络错误';
    return Promise.reject(new Error(message));
  }
);

export default request;
