export interface AboutInfo {
  id?: number;
  slogan?: string;
  summary?: string;
  content?: string;
  avatarUrl?: string;
  email?: string;
  location?: string;
  githubUrl?: string;
  csdnUrl?: string;
  linkedinUrl?: string;
  updateTime?: string;
}

export interface SkillItem {
  id?: number;
  categoryId: number;
  name: string;
  level: number;
  iconUrl?: string;
  sortOrder?: number;
  featured?: number;
}

export interface SkillCategory {
  id?: number;
  name: string;
  sortOrder?: number;
  items?: SkillItem[];
}

export interface ProjectItem {
  id?: number;
  name: string;
  description?: string;
  coverUrl?: string;
  githubUrl?: string;
  demoUrl?: string;
  featured?: number;
  sortOrder?: number;
  status?: number;
  techStack?: string[];
  updateTime?: string;
}

export interface BlogBrief {
  id: number;
  title: string;
  summary?: string;
  publishTime?: string;
}

export interface HomeData {
  about?: AboutInfo;
  featuredSkills: SkillItem[];
  featuredProjects: ProjectItem[];
  latestBlogs: BlogBrief[];
}

export interface AboutSaveRequest {
  slogan?: string;
  summary?: string;
  content?: string;
  avatarUrl?: string;
  email?: string;
  location?: string;
  githubUrl?: string;
  csdnUrl?: string;
  linkedinUrl?: string;
}

export interface SkillCategorySaveRequest {
  name: string;
  sortOrder?: number;
}

export interface SkillItemSaveRequest {
  categoryId: number;
  name: string;
  level?: number;
  iconUrl?: string;
  sortOrder?: number;
  featured?: number;
}

export interface ProjectSaveRequest {
  name: string;
  description?: string;
  coverUrl?: string;
  githubUrl?: string;
  demoUrl?: string;
  featured?: number;
  sortOrder?: number;
  status?: number;
  techStack?: string[];
}
