import request from '@/utils/request';
import { ApiResult } from '@/types/auth';
import {
  BlogArticleDetail,
  BlogArticleListItem,
  BlogCategory,
  BlogSaveRequest,
  BatchImportRequest,
  BatchImportResult,
  PageResult,
} from '@/types/blog';

/** 公开：分页列表 */
export async function fetchBlogPage(params: {
  page?: number;
  size?: number;
  keyword?: string;
  categoryId?: number;
}): Promise<PageResult<BlogArticleListItem>> {
  const res = await request.get<ApiResult<PageResult<BlogArticleListItem>>>('/api/blogs', { params });
  return res.data.data;
}

/** 公开：最新文章 */
export async function fetchLatestBlogs(limit = 5): Promise<BlogArticleListItem[]> {
  const res = await request.get<ApiResult<BlogArticleListItem[]>>('/api/blogs/latest', {
    params: { limit },
  });
  return res.data.data;
}

/** 公开：文章详情 */
export async function fetchBlogDetail(id: number): Promise<BlogArticleDetail> {
  const res = await request.get<ApiResult<BlogArticleDetail>>(`/api/blogs/${id}`);
  return res.data.data;
}

/** 公开：分类列表 */
export async function fetchBlogCategories(): Promise<BlogCategory[]> {
  const res = await request.get<ApiResult<BlogCategory[]>>('/api/blogs/categories');
  return res.data.data;
}

/** Admin：分页列表 */
export async function fetchAdminBlogPage(params: {
  page?: number;
  size?: number;
  keyword?: string;
  status?: number;
}): Promise<PageResult<BlogArticleListItem>> {
  const res = await request.get<ApiResult<PageResult<BlogArticleListItem>>>('/api/blogs/admin', {
    params,
  });
  return res.data.data;
}

/** Admin：详情 */
export async function fetchAdminBlogDetail(id: number): Promise<BlogArticleDetail> {
  const res = await request.get<ApiResult<BlogArticleDetail>>(`/api/blogs/admin/${id}`);
  return res.data.data;
}

/** Admin：新建 */
export async function createBlog(data: BlogSaveRequest): Promise<number> {
  const res = await request.post<ApiResult<{ id: number }>>('/api/blogs/admin', data);
  return res.data.data.id;
}

/** Admin：更新 */
export async function updateBlog(id: number, data: BlogSaveRequest): Promise<void> {
  await request.put(`/api/blogs/admin/${id}`, data);
}

/** Admin：发布 */
export async function publishBlog(id: number): Promise<void> {
  await request.post(`/api/blogs/admin/${id}/publish`);
}

/** Admin：下架 */
export async function offlineBlog(id: number): Promise<void> {
  await request.post(`/api/blogs/admin/${id}/offline`);
}

/** Admin：删除 */
export async function deleteBlog(id: number): Promise<void> {
  await request.delete(`/api/blogs/admin/${id}`);
}

/** Admin：导入 Markdown 文件 */
export async function importMarkdownFile(
  file: File,
  title?: string,
  status = 0
): Promise<number> {
  const form = new FormData();
  form.append('file', file);
  if (title) form.append('title', title);
  form.append('status', String(status));
  const res = await request.post<ApiResult<{ id: number }>>('/api/blogs/admin/import-md', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data.data.id;
}

/** Admin：批量导入 CSDN 导出包（backend/output） */
export async function batchImportBlogs(
  options?: BatchImportRequest
): Promise<BatchImportResult> {
  const res = await request.post<ApiResult<BatchImportResult>>(
    '/api/blogs/admin/import/batch',
    options ?? { skipExisting: true, publish: true }
  );
  return res.data.data;
}
