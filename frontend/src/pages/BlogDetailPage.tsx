import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Card, Spin, Tag, Typography, Space, Button, message } from 'antd';
import { ArrowLeftOutlined, EyeOutlined, CalendarOutlined } from '@ant-design/icons';
import { fetchBlogDetail } from '@/api/blog';
import { BlogArticleDetail } from '@/types/blog';
import MarkdownViewer from '@/components/MarkdownViewer';

const { Title, Text } = Typography;

/**
 * 博客详情页
 * 从 API 获取 Markdown 原文，由 MarkdownViewer 渲染。
 */
export default function BlogDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [loading, setLoading] = useState(true);
  const [article, setArticle] = useState<BlogArticleDetail | null>(null);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    fetchBlogDetail(Number(id))
      .then(setArticle)
      .catch((e) => message.error(e.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '80px auto' }} />;
  }

  if (!article) {
    return null;
  }

  return (
    <div style={{ maxWidth: 860, margin: '0 auto' }}>
      <Button type="link" icon={<ArrowLeftOutlined />} style={{ paddingLeft: 0 }}>
        <Link to="/blogs">返回列表</Link>
      </Button>

      <Card>
        <Title level={2}>{article.title}</Title>
        <Space wrap style={{ marginBottom: 24 }}>
          {article.categories?.map((c) => (
            <Tag key={c.id} color="blue">{c.name}</Tag>
          ))}
          <Text type="secondary"><CalendarOutlined /> {article.publishTime?.slice(0, 16)}</Text>
          <Text type="secondary"><EyeOutlined /> {article.viewCount} 阅读</Text>
          {article.authorName && <Text type="secondary">{article.authorName}</Text>}
        </Space>

        <MarkdownViewer content={article.content} />
      </Card>
    </div>
  );
}
