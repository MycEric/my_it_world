import { useEffect, useState } from 'react';
import { Card, Input, List, Pagination, Space, Tag, Typography, Empty, Spin } from 'antd';
import { EyeOutlined, CalendarOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import { fetchBlogCategories, fetchBlogPage } from '@/api/blog';
import { BlogArticleListItem, BlogCategory } from '@/types/blog';

const { Title, Paragraph, Text } = Typography;
const { Search } = Input;

/**
 * 博客列表页（公开）
 * 展示已发布的 Markdown 博客，支持搜索与分类筛选。
 */
export default function BlogListPage() {
  const [loading, setLoading] = useState(false);
  const [records, setRecords] = useState<BlogArticleListItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [categoryId, setCategoryId] = useState<number | undefined>();
  const [categories, setCategories] = useState<BlogCategory[]>([]);

  const pageSize = 10;

  useEffect(() => {
    fetchBlogCategories().then(setCategories).catch(() => {});
  }, []);

  useEffect(() => {
    loadData();
  }, [page, keyword, categoryId]);

  const loadData = async () => {
    setLoading(true);
    try {
      const result = await fetchBlogPage({ page, size: pageSize, keyword, categoryId });
      setRecords(result.records);
      setTotal(result.total);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 960, margin: '0 auto' }}>
      <Title level={2}>技术博客</Title>
      <Paragraph type="secondary">本站博客以 Markdown 格式存储与展示</Paragraph>

      <Space direction="vertical" size="middle" style={{ width: '100%', marginBottom: 24 }}>
        <Search
          placeholder="搜索标题或摘要"
          allowClear
          onSearch={(v) => { setPage(1); setKeyword(v); }}
          style={{ maxWidth: 400 }}
        />
        <Space wrap>
          <Tag
            color={categoryId === undefined ? 'blue' : 'default'}
            style={{ cursor: 'pointer' }}
            onClick={() => { setCategoryId(undefined); setPage(1); }}
          >
            全部
          </Tag>
          {categories.map((c) => (
            <Tag
              key={c.id}
              color={categoryId === c.id ? 'blue' : 'default'}
              style={{ cursor: 'pointer' }}
              onClick={() => { setCategoryId(c.id); setPage(1); }}
            >
              {c.name}
            </Tag>
          ))}
        </Space>
      </Space>

      <Spin spinning={loading}>
        {records.length === 0 ? (
          <Empty description="暂无文章" />
        ) : (
          <List
            itemLayout="vertical"
            dataSource={records}
            renderItem={(item) => (
              <List.Item key={item.id}>
                <Card hoverable>
                  <Link to={`/blogs/${item.id}`}>
                    <Title level={4} style={{ marginBottom: 8 }}>{item.title}</Title>
                  </Link>
                  <Paragraph type="secondary" ellipsis={{ rows: 2 }}>
                    {item.summary}
                  </Paragraph>
                  <Space wrap size="small">
                    {item.categories?.map((c) => (
                      <Tag key={c.id}>{c.name}</Tag>
                    ))}
                    <Text type="secondary">
                      <CalendarOutlined /> {item.publishTime?.slice(0, 10)}
                    </Text>
                    <Text type="secondary">
                      <EyeOutlined /> {item.viewCount}
                    </Text>
                    {item.authorName && <Text type="secondary">{item.authorName}</Text>}
                  </Space>
                </Card>
              </List.Item>
            )}
          />
        )}
      </Spin>

      {total > pageSize && (
        <div style={{ textAlign: 'center', marginTop: 24 }}>
          <Pagination
            current={page}
            total={total}
            pageSize={pageSize}
            onChange={setPage}
            showTotal={(t) => `共 ${t} 篇`}
          />
        </div>
      )}
    </div>
  );
}
