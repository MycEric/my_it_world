import { useEffect, useRef, useState } from 'react';
import {
  Card,
  Input,
  Button,
  Space,
  Typography,
  Spin,
  Avatar,
  Layout,
  List,
  Popconfirm,
} from 'antd';
import { RobotOutlined, UserOutlined, SendOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { useAuthStore } from '@/stores/authStore';
import { useChatStore } from '@/stores/chatStore';

const { Title, Paragraph, Text } = Typography;
const { TextArea } = Input;
const { Sider, Content } = Layout;

/**
 * AI 助手页面（会话历史一期：服务端存储 + 滑动窗口 Memory）
 */
export default function AssistantPage() {
  const { isAuthenticated } = useAuthStore();
  const {
    sessions,
    sessionsLoading,
    currentSessionId,
    messages,
    messagesLoading,
    sending,
    loadSessions,
    sendMessage,
    newChat,
    selectSession,
    removeSession,
    initChat,
  } = useChatStore();

  const [input, setInput] = useState('');
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    initChat(isAuthenticated);
  }, [isAuthenticated, initChat]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, sending, messagesLoading]);

  const handleSend = async () => {
    const text = input.trim();
    if (!text || sending) return;
    setInput('');
    await sendMessage(text);
  };

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto' }}>
      <Title level={2}>
        <RobotOutlined /> AI 助手
      </Title>
      <Paragraph type="secondary">
        基于通义千问；对话历史保存在服务端，刷新页面可恢复（游客通过本地 session_id）。
      </Paragraph>

      <Layout style={{ background: 'transparent', gap: 16 }}>
        {isAuthenticated && (
          <Sider
            width={260}
            theme="light"
            style={{
              background: '#fff',
              borderRadius: 8,
              border: '1px solid #f0f0f0',
              padding: 12,
            }}
          >
            <Button
              type="primary"
              icon={<PlusOutlined />}
              block
              onClick={newChat}
              style={{ marginBottom: 12 }}
            >
              新对话
            </Button>
            {sessionsLoading ? (
              <Spin style={{ display: 'block', textAlign: 'center', padding: 24 }} />
            ) : (
              <List
                size="small"
                dataSource={sessions}
                locale={{ emptyText: '暂无会话' }}
                renderItem={(item) => (
                  <List.Item
                    style={{
                      cursor: 'pointer',
                      padding: '8px 4px',
                      background:
                        item.session_id === currentSessionId ? '#e6f4ff' : 'transparent',
                      borderRadius: 6,
                    }}
                    onClick={() => selectSession(item.session_id)}
                    actions={[
                      <Popconfirm
                        title="删除此会话？"
                        onConfirm={() => removeSession(item.session_id)}
                        okText="删除"
                        cancelText="取消"
                      >
                        <Button
                          type="text"
                          size="small"
                          icon={<DeleteOutlined />}
                          onClick={(e) => e.stopPropagation()}
                        />
                      </Popconfirm>,
                    ]}
                  >
                    <Text ellipsis style={{ maxWidth: 180 }}>
                      {item.title || '新对话'}
                    </Text>
                  </List.Item>
                )}
              />
            )}
          </Sider>
        )}

        <Content>
          {!isAuthenticated && (
            <Button icon={<PlusOutlined />} onClick={newChat} style={{ marginBottom: 12 }}>
              新对话
            </Button>
          )}

          <Card
            style={{ minHeight: 480, marginBottom: 16 }}
            bodyStyle={{ maxHeight: 520, overflowY: 'auto' }}
          >
            {messagesLoading ? (
              <div style={{ textAlign: 'center', padding: 80 }}>
                <Spin tip="加载历史..." />
              </div>
            ) : (
              <>
                {messages.length === 0 && !sending && (
                  <Paragraph type="secondary" style={{ textAlign: 'center', marginTop: 80 }}>
                    输入问题开始对话，例如：「Spring Cloud Gateway 是做什么的？」
                  </Paragraph>
                )}

                {messages.map((msg, idx) => (
                  <div
                    key={msg.id ?? idx}
                    style={{
                      display: 'flex',
                      gap: 12,
                      marginBottom: 16,
                      flexDirection: msg.role === 'user' ? 'row-reverse' : 'row',
                    }}
                  >
                    <Avatar
                      icon={msg.role === 'user' ? <UserOutlined /> : <RobotOutlined />}
                      style={{
                        background: msg.role === 'user' ? '#1677ff' : '#52c41a',
                        flexShrink: 0,
                      }}
                    />
                    <div
                      style={{
                        maxWidth: '75%',
                        padding: '10px 14px',
                        borderRadius: 8,
                        background: msg.role === 'user' ? '#e6f4ff' : '#f6ffed',
                        whiteSpace: 'pre-wrap',
                        wordBreak: 'break-word',
                      }}
                    >
                      {msg.content}
                    </div>
                  </div>
                ))}

                {sending && (
                  <div style={{ textAlign: 'center', padding: 16 }}>
                    <Spin tip="AI 思考中..." />
                  </div>
                )}
                <div ref={bottomRef} />
              </>
            )}
          </Card>

          <Space.Compact style={{ width: '100%' }}>
            <TextArea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="输入你的问题..."
              autoSize={{ minRows: 2, maxRows: 4 }}
              onPressEnter={(e) => {
                if (!e.shiftKey) {
                  e.preventDefault();
                  handleSend();
                }
              }}
              disabled={sending || messagesLoading}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handleSend}
              loading={sending}
              disabled={messagesLoading}
              style={{ height: 'auto', minHeight: 52 }}
            >
              发送
            </Button>
          </Space.Compact>

          {currentSessionId && (
            <Text type="secondary" style={{ fontSize: 12, marginTop: 8, display: 'block' }}>
              会话 ID：{currentSessionId}
            </Text>
          )}
        </Content>
      </Layout>
    </div>
  );
}