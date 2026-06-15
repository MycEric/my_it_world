import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';

interface MarkdownViewerProps {
  /** Markdown 原文 */
  content: string;
  className?: string;
}

/**
 * Markdown 渲染组件
 * <p>
 * 使用 react-markdown + remark-gfm 支持表格、任务列表等 GFM 语法；
 * 代码块使用 syntax-highlighter 高亮。
 * </p>
 */
export default function MarkdownViewer({ content, className }: MarkdownViewerProps) {
  return (
    <div className={className} style={{ lineHeight: 1.8, fontSize: 16 }}>
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={{
          code({ className: codeClass, children, ...props }) {
            const match = /language-(\w+)/.exec(codeClass || '');
            const codeStr = String(children).replace(/\n$/, '');
            if (match) {
              return (
                <SyntaxHighlighter
                  style={oneDark}
                  language={match[1]}
                  PreTag="div"
                >
                  {codeStr}
                </SyntaxHighlighter>
              );
            }
            return (
              <code className={codeClass} {...props}>
                {children}
              </code>
            );
          },
        }}
      >
        {content}
      </ReactMarkdown>
    </div>
  );
}
