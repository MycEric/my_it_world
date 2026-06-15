package com.myitworld.content.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myitworld.content.entity.*;
import com.myitworld.content.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 首次启动写入演示数据（表为空时）
 */
@Component
@RequiredArgsConstructor
public class ContentDataInitializer implements CommandLineRunner {

    private final AboutInfoMapper aboutInfoMapper;
    private final SkillCategoryMapper categoryMapper;
    private final SkillItemMapper itemMapper;
    private final ProjectMapper projectMapper;
    private final ProjectTechStackMapper techStackMapper;

    @Override
    public void run(String... args) {
        if (aboutInfoMapper.selectById(1L) == null) {
            seedAbout();
        }
        if (categoryMapper.selectCount(null) == 0) {
            seedSkills();
        }
        if (projectMapper.selectCount(null) == 0) {
            seedProjects();
        }
    }

    private void seedAbout() {
        AboutInfo about = new AboutInfo();
        about.setId(1L);
        about.setSlogan("用代码记录成长，用博客分享思考");
        about.setSummary("Java / Spring Cloud 微服务学习者，热爱后端架构与全栈实践，正在构建个人 IT 学习成果展示平台 My IT World。");
        about.setContent("""
## 你好，我是 My IT World 的创作者

我是一名专注于 **Java 后端与微服务架构** 的学习者，长期在技术社区（如 CSDN）记录学习笔记。

### 当前方向
- Spring Boot / Spring Cloud Alibaba 微服务
- React + TypeScript 前端工程化
- Python AI 服务（通义千问、RAG 实践）

### 这个网站
本站是我个人作品集与学习实验场：博客沉淀、项目展示、以及基于站内知识的 AI 助手。

欢迎通过下方链接关注我，或通过 AI 助手提问技术问题。
""");
        about.setEmail("demo@myitworld.dev");
        about.setLocation("中国");
        about.setGithubUrl("https://github.com");
        about.setCsdnUrl("https://blog.csdn.net");
        aboutInfoMapper.insert(about);
    }

    private void seedSkills() {
        long lang = insertCategory("编程语言", 1);
        long framework = insertCategory("框架与中间件", 2);
        long tool = insertCategory("工具与其它", 3);

        insertItem(lang, "Java", 5, 1, 1);
        insertItem(lang, "Python", 4, 2, 1);
        insertItem(lang, "JavaScript / TypeScript", 3, 3, 0);
        insertItem(framework, "Spring Boot", 5, 1, 1);
        insertItem(framework, "Spring Cloud Alibaba", 4, 2, 1);
        insertItem(framework, "MyBatis-Plus", 4, 3, 0);
        insertItem(framework, "Redis", 4, 4, 1);
        insertItem(framework, "MySQL", 4, 5, 0);
        insertItem(tool, "Docker", 3, 1, 1);
        insertItem(tool, "Git", 4, 2, 0);
        insertItem(tool, "React", 3, 3, 1);
    }

    private long insertCategory(String name, int order) {
        SkillCategory cat = new SkillCategory();
        cat.setName(name);
        cat.setSortOrder(order);
        categoryMapper.insert(cat);
        return cat.getId();
    }

    private void insertItem(long categoryId, String name, int level, int order, int featured) {
        SkillItem item = new SkillItem();
        item.setCategoryId(categoryId);
        item.setName(name);
        item.setLevel(level);
        item.setSortOrder(order);
        item.setFeatured(featured);
        itemMapper.insert(item);
    }

    private void seedProjects() {
        insertProject(
                "My IT World 个人站",
                "基于 React + Spring Cloud Alibaba + Python AI 的个人品牌网站，集成博客、管理后台与 AI 助手。",
                1, 1,
                "https://github.com",
                null,
                List.of("React", "Spring Cloud", "FastAPI", "MySQL", "Redis"));
        insertProject(
                "博客与 Markdown 中心",
                "blog-service 支持 Markdown 存储、在线编辑、CSDN 导出批量导入与静态资源托管。",
                1, 2,
                "https://github.com",
                null,
                List.of("Spring Boot", "MyBatis-Plus", "Markdown"));
        insertProject(
                "AI 对话服务",
                "ai-service 接入通义千问，实现多轮对话与服务端会话历史持久化，为后续 RAG 预留扩展。",
                1, 3,
                "https://github.com",
                null,
                List.of("Python", "FastAPI", "LangChain", "通义千问"));
    }

    private void insertProject(String name, String desc, int featured, int order,
                               String github, String demo, List<String> techs) {
        Project p = new Project();
        p.setName(name);
        p.setDescription(desc);
        p.setFeatured(featured);
        p.setSortOrder(order);
        p.setStatus(1);
        p.setGithubUrl(github);
        p.setDemoUrl(demo);
        projectMapper.insert(p);
        int i = 0;
        for (String tech : techs) {
            ProjectTechStack stack = new ProjectTechStack();
            stack.setProjectId(p.getId());
            stack.setTechName(tech);
            stack.setSortOrder(i++);
            techStackMapper.insert(stack);
        }
    }
}
