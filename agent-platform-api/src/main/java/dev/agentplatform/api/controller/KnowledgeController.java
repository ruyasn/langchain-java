package dev.agentplatform.api.controller;

import dev.agentplatform.common.model.Result;
import dev.agentplatform.common.model.knowledge.DocumentChunk;
import dev.agentplatform.common.model.knowledge.KnowledgeBase;
import dev.agentplatform.knowledge.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 知识库 REST API
 */
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping("/bases")
    public Result<KnowledgeBase> createBase(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        KnowledgeBase kb = knowledgeService.createKnowledgeBase(name, description != null ? description : "");
        return Result.ok(kb);
    }

    @GetMapping("/bases")
    public Result<List<KnowledgeBase>> listBases() {
        return Result.ok(knowledgeService.listKnowledgeBases());
    }

    @GetMapping("/bases/{id}")
    public Result<KnowledgeBase> getBase(@PathVariable String id) {
        KnowledgeBase kb = knowledgeService.getKnowledgeBase(id);
        if (kb == null) return Result.fail(404, "Knowledge base not found");
        return Result.ok(kb);
    }

    @PostMapping(value = "/bases/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Void> addDocument(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            knowledgeService.addDocument(id, file.getOriginalFilename(), is);
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
        return Result.ok(null);
    }

    @PostMapping("/bases/{id}/text")
    public Result<Void> addText(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String text = body != null ? body.get("text") : null;
        String source = body != null ? body.get("source") : "api";
        if (text == null) return Result.fail(400, "text required");
        knowledgeService.addText(id, text, source);
        return Result.ok(null);
    }

    @GetMapping("/bases/{id}/search")
    public Result<List<DocumentChunk>> search(
            @PathVariable String id,
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int maxResults) {
        List<DocumentChunk> chunks = knowledgeService.search(id, q, maxResults);
        return Result.ok(chunks);
    }

    @GetMapping("/bases/{id}/retrieve")
    public Result<String> retrieve(
            @PathVariable String id,
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int maxResults,
            @RequestParam(defaultValue = "2000") int maxChars) {
        String content = knowledgeService.retrieveForPrompt(id, q, maxResults, maxChars);
        return Result.ok(content);
    }
}
