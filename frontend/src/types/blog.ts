/** 博客分类 */
export interface BlogCategory {
  id: number;
  name: string;
  slug: string;
  sortOrder?: number;
}

/** 博客列表项 */
export interface BlogArticleListItem {
  id: number;
  title: string;
  summary: string;
  cover?: string;
  source: string;
  status: number;
  publishTime?: string;
  viewCount: number;
  authorName?: string;
  categories?: { id: number; name: string; slug: string }[];
}

/** 博客详情（含 Markdown 正文） */
export interface BlogArticleDetail extends BlogArticleListItem {
  content: string;
  sourceUrl?: string;
  createTime?: string;
  updateTime?: string;
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[];
  total: number;
  page: number;
  size: number;
}

/** 保存博客请求 */
export interface BlogSaveRequest {
  title: string;
  summary?: string;
  content: string;
  cover?: string;
  status: number;
  categoryIds?: number[];
}

/** 文章状态 */
export const ARTICLE_STATUS = {
  DRAFT: 0,
  PUBLISHED: 1,
  OFFLINE: 2,
} as const;

export const STATUS_LABEL: Record<number, string> = {
  0: '草稿',
  1: '已发布',
  2: '已下架',
};

/** 批量导入结果 */
export interface BatchImportResult {
  total: number;
  imported: number;
  skipped: number;
  updated: number;
  failed: number;
  errors: string[];
}

/** 批量导入选项 */
export interface BatchImportRequest {
  skipExisting?: boolean;
  updateExisting?: boolean;
  publish?: boolean;
}
