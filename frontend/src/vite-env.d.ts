/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** API 基础路径，开发环境通过 Vite 代理，生产环境指向网关 */
  readonly VITE_API_BASE_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
