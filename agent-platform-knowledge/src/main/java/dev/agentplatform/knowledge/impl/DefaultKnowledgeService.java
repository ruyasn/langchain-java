package dev.agentplatform.knowledge.impl;

import dev.agentplatform.common.model.knowledge.DocumentChunk;
import dev.agentplatform.common.model.knowledge.KnowledgeBase;
import dev.agentplatform.knowledge.KnowledgeService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 默认知识库服务：使用 LangChain4j EmbeddingModel + EmbeddingStore（内存）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultKnowledgeService implements KnowledgeService {

    private final EmbeddingModel embeddingModel;
    private final Map<String, KnowledgeBase> knowledgeBases = new ConcurrentHashMap<>();
    private final Map<String, EmbeddingStore<TextSegment>> stores = new ConcurrentHashMap<>();

    @Override
    public KnowledgeBase createKnowledgeBase(String name, String description) {
        String id = "kb-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        KnowledgeBase kb = KnowledgeBase.builder()
                .id(id)
                .name(name)
                .description(description)
                .embeddingModel("default")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        knowledgeBases.put(id, kb);
        stores.put(id, new InMemoryEmbeddingStore<>());
        log.info("Created knowledge base: {}", id);
        return kb;
    }

    @Override
    public KnowledgeBase getKnowledgeBase(String id) {
        return knowledgeBases.get(id);
    }

    @Override
    public List<KnowledgeBase> listKnowledgeBases() {
        return List.copyOf(knowledgeBases.values());
    }

    @Override
    public void addDocument(String knowledgeBaseId, String fileName, InputStream content) {
        EmbeddingStore<TextSegment> store = stores.get(knowledgeBaseId);
        if (store == null) throw new IllegalArgumentException("Knowledge base not found: " + knowledgeBaseId);
        DocumentParser parser = new TextDocumentParser();
        try {
            Document doc = parser.parse(content);
            DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
            List<TextSegment> segments = splitter.splitAll(List.of(doc));
            for (TextSegment seg : segments) {
                Embedding emb = embeddingModel.embed(seg).content();
                store.add(emb, seg);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add document", e);
        }
    }

    @Override
    public void addText(String knowledgeBaseId, String text, String source) {
        addDocument(knowledgeBaseId, source, new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public List<DocumentChunk> search(String knowledgeBaseId, String query, int maxResults) {
        EmbeddingStore<TextSegment> store = stores.get(knowledgeBaseId);
        if (store == null) throw new IllegalArgumentException("Knowledge base not found: " + knowledgeBaseId);
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        List<EmbeddingMatch<TextSegment>> matches = store.findRelevant(queryEmbedding, maxResults);
        return matches.stream()
                .map(m -> DocumentChunk.builder()
                        .id(UUID.randomUUID().toString())
                        .knowledgeBaseId(knowledgeBaseId)
                        .content(m.embedded().text())
                        .source("")
                        .embedding(null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String retrieveForPrompt(String knowledgeBaseId, String query, int maxResults, int maxChars) {
        List<DocumentChunk> chunks = search(knowledgeBaseId, query, maxResults);
        StringBuilder sb = new StringBuilder();
        for (DocumentChunk c : chunks) {
            if (sb.length() + c.getContent().length() > maxChars) break;
            sb.append(c.getContent()).append("\n\n");
        }
        return sb.toString();
    }
}
