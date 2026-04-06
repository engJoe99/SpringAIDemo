package com.boghdady.springaidemo.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    // ── Configuration Constants ───────────────────────────────────────────────
    private static final int CHUNK_SIZE = 800;       // Tokens per chunk
    private static final int CHUNK_OVERLAP = 150;    // Overlap between chunks
    private static final int MIN_CHUNK_CHARS = 50;   // Skip tiny chunks

    @Async
    public CompletableFuture<IngestionResult> ingestPdfAsync(Resource resource,
                                                             Map<String, Object> metadata) {
        try {
            int chunks = ingestPdf(resource, metadata);
            return CompletableFuture.completedFuture(
                    new IngestionResult(resource.getFilename(), chunks, "SUCCESS", null)
            );
        } catch (Exception e) {
            log.info("Ingestion failed for {} : {}", resource.getFilename(), e.getMessage());
            return CompletableFuture.completedFuture(
                    new IngestionResult(resource.getFilename(), 0, "FAILED", e.getMessage())
            );
        }
    }

    /**
     * Synchronous PDF ingestion.
     * Full pipeline: Load → Split → (Embed) → Store
     */
    public int ingestPdf(Resource resource, Map<String, Object> additionalMetadata) {

        log.info("Starting Ingestion For: {}", resource.getFilename());

        // ── LOAD ──────────────────────────────────────────────────────────────
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        ExtractedTextFormatter.builder()
                                // Don't skip any pages when cleaning up text (start from page 1)
                                .withNumberOfTopPagesToSkipBeforeDelete(0)
                                .build()
                )
                // Split the PDF so each page becomes a separate document
                .withPagesPerDocument(1)
                .build();

        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);
        List<Document> pages = reader.get();

        // Enrich each page's metadata with additional context
        pages.forEach(page -> {
            page.getMetadata().put("source", resource.getFilename());
            page.getMetadata().put("ingested_at", System.currentTimeMillis());
            page.getMetadata().putAll(additionalMetadata);
        });

        log.info("Loaded {} pages from {}", pages.size(), resource.getFilename());

        // ── SPLIT ─────────────────────────────────────────────────────────────
        TokenTextSplitter splitter = new TokenTextSplitter(
                CHUNK_SIZE,
                CHUNK_OVERLAP,
                MIN_CHUNK_CHARS,
                10000,
                true
        );
        List<Document> chunks = splitter.apply(pages);
        log.info("Split into {} chunks", chunks.size());

        if (chunks.isEmpty()) {
            log.warn("No chunks produced from {}. Skipping ingestion.", resource.getFilename());
            return 0;
        }

        // ── EMBED + STORE ──────────────────────────────────────────────────────
        // VectorStore.add() handles embedding generation internally.
        vectorStore.add(chunks);
        log.info("Successfully ingested {} chunks from {}", chunks.size(), resource.getFilename());
        return chunks.size();
    }

    /**
     * Ingest plain text or Markdown files.
     */
    public int ingestText(Resource resource, Map<String, Object> metadata) {
        TextReader reader = new TextReader(resource);
        List<Document> docs = reader.get();
        docs.forEach(d -> {
            d.getMetadata().put("source", resource.getFilename());
            d.getMetadata().putAll(metadata);
        });

        List<Document> chunks = new TokenTextSplitter(
                CHUNK_SIZE,
                CHUNK_OVERLAP,
                50,
                100,
                true
        ).apply(docs);
        return chunks.size();
    }


    /**
     * Ingest a list of manually created Documents (for structured data, APIs, DB).
     */
    public int ingestDocuments(List<Document> documents) {
        List<Document> chunks = new TokenTextSplitter().apply(documents);
        vectorStore.add(chunks);
        return chunks.size();
    }

    public record IngestionResult(String filename, int chunks, String status, String error) {}

}
