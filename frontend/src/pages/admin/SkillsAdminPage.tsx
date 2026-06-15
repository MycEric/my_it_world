import { useEffect, useState } from 'react';
import {
  Card, Table, Button, Space, Modal, Form, Input, InputNumber, Select, Switch, message, Popconfirm, Tabs,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  createSkillCategory, createSkillItem, deleteSkillCategory, deleteSkillItem,
  fetchAdminSkillCategories, fetchAdminSkillItems, updateSkillCategory, updateSkillItem,
} from '@/api/content';
import { SkillCategory, SkillItem, SkillItemSaveRequest } from '@/types/content';

export default function SkillsAdminPage() {
  const [categories, setCategories] = useState<SkillCategory[]>([]);
  const [items, setItems] = useState<SkillItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [catModal, setCatModal] = useState(false);
  const [itemModal, setItemModal] = useState(false);
  const [editingCat, setEditingCat] = useState<SkillCategory | null>(null);
  const [editingItem, setEditingItem] = useState<SkillItem | null>(null);
  const [catForm] = Form.useForm();
  const [itemForm] = Form.useForm<SkillItemSaveRequest>();

  const load = async () => {
    setLoading(true);
    try {
      const [cats, its] = await Promise.all([
        fetchAdminSkillCategories(),
        fetchAdminSkillItems(),
      ]);
      setCategories(cats);
      setItems(its);
    } catch (e) {
      message.error(e instanceof Error ? e.message : '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const catColumns: ColumnsType<SkillCategory> = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '名称', dataIndex: 'name' },
    { title: '排序', dataIndex: 'sortOrder', width: 80 },
    {
      title: '操作',
      width: 160,
      render: (_, row) => (
        <Space>
          <Button size="small" onClick={() => {
            setEditingCat(row);
            catForm.setFieldsValue(row);
            setCatModal(true);
          }}>编辑</Button>
          <Popconfirm title="删除分类及其下技能？" onConfirm={async () => {
            await deleteSkillCategory(row.id!);
            message.success('已删除');
            load();
          }}>
            <Button size="small" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const itemColumns: ColumnsType<SkillItem> = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '名称', dataIndex: 'name' },
    { title: '分类ID', dataIndex: 'categoryId', width: 80 },
    { title: '等级', dataIndex: 'level', width: 60 },
    { title: '排序', dataIndex: 'sortOrder', width: 60 },
    { title: '精选', dataIndex: 'featured', width: 60, render: (v) => v === 1 ? '是' : '否' },
    {
      title: '操作',
      width: 160,
      render: (_, row) => (
        <Space>
          <Button size="small" onClick={() => {
            setEditingItem(row);
            itemForm.setFieldsValue({
              ...row,
              featured: row.featured === 1,
            });
            setItemModal(true);
          }}>编辑</Button>
          <Popconfirm title="确认删除？" onConfirm={async () => {
            await deleteSkillItem(row.id!);
            message.success('已删除');
            load();
          }}>
            <Button size="small" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const saveCategory = async () => {
    const values = await catForm.validateFields();
    if (editingCat?.id) {
      await updateSkillCategory(editingCat.id, values);
    } else {
      await createSkillCategory(values);
    }
    message.success('保存成功');
    setCatModal(false);
    setEditingCat(null);
    catForm.resetFields();
    load();
  };

  const saveItem = async () => {
    const values = await itemForm.validateFields();
    const payload = {
      ...values,
      featured: values.featured ? 1 : 0,
    };
    if (editingItem?.id) {
      await updateSkillItem(editingItem.id, payload);
    } else {
      await createSkillItem(payload);
    }
    message.success('保存成功');
    setItemModal(false);
    setEditingItem(null);
    itemForm.resetFields();
    load();
  };

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto' }}>
      <Tabs
        items={[
          {
            key: 'cat',
            label: '技能分类',
            children: (
              <Card extra={
                <Button type="primary" onClick={() => {
                  setEditingCat(null);
                  catForm.resetFields();
                  setCatModal(true);
                }}>新建分类</Button>
              }>
                <Table rowKey="id" loading={loading} columns={catColumns} dataSource={categories} pagination={false} />
              </Card>
            ),
          },
          {
            key: 'item',
            label: '技能项',
            children: (
              <Card extra={
                <Button type="primary" onClick={() => {
                  setEditingItem(null);
                  itemForm.resetFields();
                  setItemModal(true);
                }}>新建技能</Button>
              }>
                <Table rowKey="id" loading={loading} columns={itemColumns} dataSource={items} pagination={false} />
              </Card>
            ),
          },
        ]}
      />

      <Modal
        title={editingCat ? '编辑分类' : '新建分类'}
        open={catModal}
        onOk={saveCategory}
        onCancel={() => setCatModal(false)}
      >
        <Form form={catForm} layout="vertical">
          <Form.Item name="name" label="名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序">
            <InputNumber min={0} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={editingItem ? '编辑技能' : '新建技能'}
        open={itemModal}
        onOk={saveItem}
        onCancel={() => setItemModal(false)}
        width={520}
      >
        <Form form={itemForm} layout="vertical" initialValues={{ level: 3, featured: 0 }}>
          <Form.Item name="categoryId" label="分类" rules={[{ required: true }]}>
            <Select options={categories.map((c) => ({ label: c.name, value: c.id }))} />
          </Form.Item>
          <Form.Item name="name" label="技能名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="level" label="熟练度 1-5">
            <InputNumber min={1} max={5} />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序">
            <InputNumber min={0} />
          </Form.Item>
          <Form.Item name="featured" label="首页精选" valuePropName="checked">
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
