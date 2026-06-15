import { useEffect, useState } from 'react';
import { Card, Spin, Typography, Tag, Progress, Row, Col } from 'antd';
import { fetchSkills } from '@/api/content';
import { SkillCategory } from '@/types/content';

const { Title, Paragraph } = Typography;

export default function SkillsPage() {
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState<SkillCategory[]>([]);

  useEffect(() => {
    fetchSkills()
      .then(setCategories)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '80px auto' }} />;
  }

  return (
    <div style={{ maxWidth: 960, margin: '0 auto' }}>
      <Title level={2}>技能全景</Title>
      <Paragraph type="secondary">按分类展示技术栈与熟练度（1-5）</Paragraph>

      {categories.map((cat) => (
        <Card key={cat.id} title={cat.name} style={{ marginBottom: 16 }}>
          <Row gutter={[16, 16]}>
            {(cat.items ?? []).map((item) => (
              <Col xs={24} sm={12} md={8} key={item.id}>
                <div>
                  <div style={{ marginBottom: 4 }}>
                    <Tag color="geekblue">{item.name}</Tag>
                    {item.featured === 1 && <Tag color="gold">精选</Tag>}
                  </div>
                  <Progress percent={item.level * 20} showInfo={false} size="small" />
                </div>
              </Col>
            ))}
          </Row>
        </Card>
      ))}
    </div>
  );
}
