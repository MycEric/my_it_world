import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Button, Card, Form, Input, Select, Space, message, Row, Col, Typography,
} from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import {
  createBlog, fetchAdminBlogDetail, updateBlog,
} from '@/api/blog';
import { ARTICLE_STATUS, BlogSaveRequest } from '@/types/blog';
import MarkdownViewer from '@/components/MarkdownViewer';

const { TextArea } = Input;
const { Title } = Typography;

/**
 * Admin 博客编辑页
 * 左侧 Markdown 编辑，右侧实时预览（类似 Typora 分栏）。
 */
export default function BlogEditPage() {
  const { id } = useParams<{ id: string }>();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const [form] = Form.useForm<BlogSaveRequest>();
  const [loading, setLoading] = useState(false);
  const [preview, setPreview] = useState('');

  useEffect(() => {
    if (isEdit && id) {
      setLoading(true);
      fetchAdminBlogDetail(Number(id))
        .then((detail) => {
          form.setFieldsValue({
            title: detail.title,
            summary: detail.summary,
            content: detail.content,
            cover: detail.cover,
            status: detail.status,
          });
          setPreview(detail.content);
        })
        .catch((e) => message.error(e.message))
        .finally(() => setLoading(false));
    } else {
      form.setFieldsValue({
        status: ARTICLE_STATUS.DRAFT,
        content: '# 文章标题\n\n在此编写 Markdown 正文...',
      });
      setPreview('# 文章标题\n\n在此编写 Markdown 正文...');
    }
  }, [id, isEdit, form]);

  const onFinish = async (values: BlogSaveRequest) => {
    setLoading(true);
    try {
      if (isEdit && id) {
        await updateBlog(Number(id), values);
        message.success('更新成功');
      } else {
        const newId = await createBlog(values);
        message.success('创建成功');
        navigate(`/admin/blogs/edit/${newId}`, { replace: true });
        return;
      }
    } catch (e) {
      message.error(e instanceof Error ? e.message : '保存失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Button
        type="link"
        icon={<ArrowLeftOutlined />}
        onClick={() => navigate('/admin/blogs')}
        style={{ paddingLeft: 0, marginBottom: 8 }}
      >
        返回列表
      </Button>

      <Title level={4}>{isEdit ? '编辑文章' : '新建文章'}</Title>

      <Form form={form} layout="vertical" onFinish={onFinish}>
        <Row gutter={16}>
          <Col span={12}>
            <Card title="编辑" size="small">
              <Form.Item name="title" label="标题" rules={[{ required: true }]}>
                <Input placeholder="文章标题" />
              </Form.Item>
              <Form.Item name="summary" label="摘要（可选，留空自动生成）">
                <Input.TextArea rows={2} placeholder="列表页展示的摘要" />
              </Form.Item>
              <Form.Item name="cover" label="封面 URL（可选）">
                <Input placeholder="https://..." />
              </Form.Item>
              <Form.Item name="status" label="状态" rules={[{ required: true }]}>
                <Select options={[
                  { value: 0, label: '草稿' },
                  { value: 1, label: '立即发布' },
                  { value: 2, label: '下架' },
                ]} />
              </Form.Item>
              <Form.Item
                name="content"
                label="Markdown 正文"
                rules={[{ required: true, message: '请输入正文' }]}
              >
                <TextArea
                  rows={18}
                  placeholder="支持 Markdown 语法"
                  onChange={(e) => setPreview(e.target.value)}
                />
              </Form.Item>
              <Space>
                <Button type="primary" htmlType="submit" loading={loading}>保存</Button>
                <Button onClick={() => navigate('/admin/blogs')}>取消</Button>
              </Space>
            </Card>
          </Col>
          <Col span={12}>
            <Card title="预览" size="small" style={{ minHeight: 600 }}>
              <MarkdownViewer content={preview} />
            </Card>
          </Col>
        </Row>
      </Form>
    </div>
  );
}
