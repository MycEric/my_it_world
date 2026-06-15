import { create } from 'zustand';
import { getCurrentUserApi, loginApi, logoutApi, registerApi } from '@/api/auth';
import { LoginRequest, RegisterRequest, UserInfo } from '@/types/auth';
import { clearTokens, getAccessToken, isLoggedIn, saveTokens } from '@/utils/token';

/**
 * 用户认证状态 Store（Zustand）
 * <p>
 * 集中管理登录态、用户信息，供路由守卫和各页面组件使用。
 * </p>
 */
interface AuthState {
  /** 是否已登录 */
  isAuthenticated: boolean;
  /** 当前用户信息 */
  user: UserInfo | null;
  /** 是否正在加载用户信息 */
  loading: boolean;

  /** 登录 */
  login: (data: LoginRequest) => Promise<void>;
  /** 注册 */
  register: (data: RegisterRequest) => Promise<void>;
  /** 登出 */
  logout: () => Promise<void>;
  /** 拉取当前用户信息（页面刷新后恢复登录态） */
  fetchUser: () => Promise<void>;
  /** 是否为管理员 */
  isAdmin: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  isAuthenticated: isLoggedIn(),
  user: null,
  loading: false,

  login: async (data) => {
    const tokenData = await loginApi(data);
    saveTokens(tokenData.accessToken, tokenData.refreshToken);
    set({
      isAuthenticated: true,
      user: {
        userId: tokenData.userId,
        username: tokenData.username,
        roles: tokenData.roles,
      },
    });
  },

  register: async (data) => {
    const tokenData = await registerApi(data);
    saveTokens(tokenData.accessToken, tokenData.refreshToken);
    set({
      isAuthenticated: true,
      user: {
        userId: tokenData.userId,
        username: tokenData.username,
        roles: tokenData.roles,
      },
    });
  },

  logout: async () => {
    try {
      if (getAccessToken()) {
        await logoutApi();
      }
    } finally {
      clearTokens();
      set({ isAuthenticated: false, user: null });
    }
  },

  fetchUser: async () => {
    if (!getAccessToken()) {
      set({ isAuthenticated: false, user: null });
      return;
    }
    set({ loading: true });
    try {
      const user = await getCurrentUserApi();
      set({ isAuthenticated: true, user, loading: false });
    } catch {
      clearTokens();
      set({ isAuthenticated: false, user: null, loading: false });
    }
  },

  isAdmin: () => {
    const { user } = get();
    return user?.roles?.includes('ADMIN') ?? false;
  },
}));
