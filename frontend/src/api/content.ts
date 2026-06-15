import request from '@/utils/request';
import { ApiResult } from '@/types/auth';
import {
  AboutInfo,
  AboutSaveRequest,
  HomeData,
  ProjectItem,
  ProjectSaveRequest,
  SkillCategory,
  SkillCategorySaveRequest,
  SkillItem,
  SkillItemSaveRequest,
} from '@/types/content';

export async function fetchHome(): Promise<HomeData> {
  const res = await request.get<ApiResult<HomeData>>('/api/content/home');
  return res.data.data;
}

export async function fetchAbout(): Promise<AboutInfo> {
  const res = await request.get<ApiResult<AboutInfo>>('/api/content/about');
  return res.data.data;
}

export async function fetchSkills(): Promise<SkillCategory[]> {
  const res = await request.get<ApiResult<SkillCategory[]>>('/api/content/skills');
  return res.data.data;
}

export async function fetchProjects(): Promise<ProjectItem[]> {
  const res = await request.get<ApiResult<ProjectItem[]>>('/api/content/projects');
  return res.data.data;
}

export async function fetchProjectDetail(id: number): Promise<ProjectItem> {
  const res = await request.get<ApiResult<ProjectItem>>(`/api/content/projects/${id}`);
  return res.data.data;
}

export async function fetchAdminAbout(): Promise<AboutInfo> {
  const res = await request.get<ApiResult<AboutInfo>>('/api/content/admin/about');
  return res.data.data;
}

export async function updateAbout(data: AboutSaveRequest): Promise<AboutInfo> {
  const res = await request.put<ApiResult<AboutInfo>>('/api/content/admin/about', data);
  return res.data.data;
}

export async function fetchAdminSkillCategories(): Promise<SkillCategory[]> {
  const res = await request.get<ApiResult<SkillCategory[]>>('/api/content/admin/skill-categories');
  return res.data.data;
}

export async function createSkillCategory(data: SkillCategorySaveRequest): Promise<number> {
  const res = await request.post<ApiResult<{ id: number }>>('/api/content/admin/skill-categories', data);
  return res.data.data.id;
}

export async function updateSkillCategory(id: number, data: SkillCategorySaveRequest): Promise<void> {
  await request.put(`/api/content/admin/skill-categories/${id}`, data);
}

export async function deleteSkillCategory(id: number): Promise<void> {
  await request.delete(`/api/content/admin/skill-categories/${id}`);
}

export async function fetchAdminSkillItems(categoryId?: number): Promise<SkillItem[]> {
  const res = await request.get<ApiResult<SkillItem[]>>('/api/content/admin/skill-items', {
    params: categoryId ? { categoryId } : undefined,
  });
  return res.data.data;
}

export async function createSkillItem(data: SkillItemSaveRequest): Promise<number> {
  const res = await request.post<ApiResult<{ id: number }>>('/api/content/admin/skill-items', data);
  return res.data.data.id;
}

export async function updateSkillItem(id: number, data: SkillItemSaveRequest): Promise<void> {
  await request.put(`/api/content/admin/skill-items/${id}`, data);
}

export async function deleteSkillItem(id: number): Promise<void> {
  await request.delete(`/api/content/admin/skill-items/${id}`);
}

export async function fetchAdminProjects(): Promise<ProjectItem[]> {
  const res = await request.get<ApiResult<ProjectItem[]>>('/api/content/admin/projects');
  return res.data.data;
}

export async function fetchAdminProject(id: number): Promise<ProjectItem> {
  const res = await request.get<ApiResult<ProjectItem>>(`/api/content/admin/projects/${id}`);
  return res.data.data;
}

export async function createProject(data: ProjectSaveRequest): Promise<number> {
  const res = await request.post<ApiResult<{ id: number }>>('/api/content/admin/projects', data);
  return res.data.data.id;
}

export async function updateProject(id: number, data: ProjectSaveRequest): Promise<void> {
  await request.put(`/api/content/admin/projects/${id}`, data);
}

export async function deleteProject(id: number): Promise<void> {
  await request.delete(`/api/content/admin/projects/${id}`);
}
