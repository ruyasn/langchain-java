package dev.agentplatform.knowledge;

import dev.agentplatform.common.model.knowledge.KnowledgeBase;
import dev.agentplatform.common.model.knowledge.DocumentChunk;

import java.io.InputStream;
import java.util.List;

/**
 * 知识库服务：RAG 索引与检索
 */
public interface KnowledgeService {

    /**
     * 创建知识库
     */
    KnowledgeBase createKnowledgeBase(String name, String description);

    /**
     * 获取知识库
     */
    KnowledgeBase getKnowledgeBase(String id);

    /**
     * 列出所有知识库
     */
    List<KnowledgeBase> listKnowledgeBases();

    /**
     * 向知识库添加文档（支持 PDF 等，内部解析并分块嵌入）
     */
    void addDocument(String knowledgeBaseId, String fileName, InputStream content);

    /**
     * 向知识库添加纯文本
     */
    void addText(String knowledgeBaseId, String text, String source);

    /**
     * 相似度检索：返回最相关的若干 chunk
     */
    List<DocumentChunk> search(String knowledgeBaseId, String query, int maxResults);

    /**
     * RAG 查询：检索后拼接为上下文字符串，供 Agent 使用
     */
    String retrieveForPrompt(String knowledgeBaseId, String query, int maxResults, int maxChars);
}
