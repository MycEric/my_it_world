import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Button, Table, Space, Tag, Input, Select, message, Popconfirm, Upload,
} from 'antd';
import { PlusOutlined, UploadOutlined, CloudDownloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import {
  batchImportBlogs, deleteBlog, fetchAdminBlogPage, importMarkdownFile,
  offlineBlog, publishBlog,
} from '@/api/blog';
import { BlogArticleListItem, STATUS_LABEL } from '@/types/blog';

const { Search } = Input;

/**
 * Admin 博客管理列表
 */
export default function BlogAdminPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [records, setRecords] = useState<BlogArticleListItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState<number | undefined>();
  const [importing, setImporting] = useState(false);

  const loadData = async () => {
    setLoading(true);
    try {
      const result = await fetchAdminBlogPage({ page, size: 10, keyword, status });
      setRecords(result.records);
      setTotal(result.total);
    } catch (e) {
      message.error(e instanceof Error ? e.message : '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, [page, keyword, status]);

  const columns: ColumnsType<BlogArticleListItem> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '标题', dataIndex: 'title', ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      width: 90,
      render: (s: number) => {
        const color = s === 1 ? 'green' : s === 0 ? 'default' : 'orange';
        return <Tag color={color}>{STATUS_LABEL[s] ?? s}</Tag>;
      },
    },
    { title: '来源', dataIndex: 'source', width: 80 },
    { title: '浏览', dataIndex: 'viewCount', width: 70 },
    { title: '发布时间', dataIndex: 'publishTime', width: 170,
      render: (v) => v?.slice(0, 16) ?? '-' },
    {
      title: '操作',
      width: 280,
      render: (_, row) => (
        <Space size="small">
          <Button size="small" onClick={() => navigate(`/admin/blogs/edit/${row.id}`)}>编辑</Button>
          {row.status !== 1 && (
            <Button size="small" type="primary" onClick={() => handlePublish(row.id)}>发布</Button>
          )}
          {row.status === 1 && (
            <Button size="small" onClick={() => handleOffline(row.id)}>下架</Button>
          )}
          <Popconfirm title="确认删除？" onConfirm={() => handleDelete(row.id)}>
            <Button size="small" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const handlePublish = async (id: number) => {
    await publishBlog(id);
    message.success('已发布');
    loadData();
  };

  const handleOffline = async (id: number) => {
    await offlineBlog(id);
    message.success('已下架');
    loadData();
  };

  const handleDelete = async (id: number) => {
    await deleteBlog(id);
    message.success('已删除');
    loadData();
  };

  const handleBatchImport = async () => {
    setImporting(true);
    try {
      const result = await batchImportBlogs({
        skipExisting: true,
        publish: true,
      });
      message.success(
        `导入完成：新增 ${result.imported}，跳过 ${result.skipped}，失败 ${result.failed}`
      );
      if (result.errors?.length) {
        console.warn('导入错误详情', result.errors);
      }
      loadData();
    } catch (e) {
      message.error(e instanceof Error ? e.message : '批量导入失败');
    } finally {
      setImporting(false);
    }
  };

  const handleImport = async (file: File) => {
    try {
      const id = await importMarkdownFile(file);
      message.success(`导入成功，文章 ID: ${id}`);
      loadData();
    } catch (e) {
      message.error(e instanceof Error ? e.message : '导入失败');
    }
    return false;
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }} wrap>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/admin/blogs/new')}>
          新建文章
        </Button>
        <Upload accept=".md" showUploadList={false} beforeUpload={handleImport}>
          <Button icon={<UploadOutlined />}>导入 .md 文件</Button>
        </Upload>
        <Button
          icon={<CloudDownloadOutlined />}
          loading={importing}
          onClick={handleBatchImport}
        >
          批量导入 output 包
        </Button>
        <Search
          placeholder="搜索标题"
          allowClear
          onSearch={(v) => { setPage(1); setKeyword(v); }}
          style={{ width: 220 }}
        />
        <Select
          placeholder="状态筛选"
          allowClear
          style={{ width: 120 }}
          onChange={(v) => { setPage(1); setStatus(v); }}
          options={[
            { value: 0, label: '草稿' },
            { value: 1, label: '已发布' },
            { value: 2, label: '已下架' },
          ]}
        />
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={records}
        pagination={{
          current: page,
          total,
          pageSize: 10,
          onChange: setPage,
          showTotal: (t) => `共 ${t} 篇`,
        }}
      />
    </div>
  );
}
