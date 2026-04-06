package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.dto.IngestDocumentRequest;
import com.boghdady.springaidemo.ingestion.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
public class IngestionController {

    private final DocumentIngestionService ingestionService;

    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<DocumentIngestionService.IngestionResult> ingestPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category) throws Exception{

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        byte[] bytes = file.getBytes();
        Resource resource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return originalFilename;
            }
        };
        Map<String, Object> metadata = Map.of(
                "category", category,
                "original_filename", originalFilename
        );
        return ingestionService.ingestPdfAsync(resource, metadata);
    }


    @PostMapping("/docs")
    public ResponseEntity<Map<String, Integer>> ingestDocs(@RequestBody IngestDocumentRequest request) {
        var docs = request.entries().stream()
                .map(documentEntry -> new Document(
                        documentEntry.content(),
                        documentEntry.metadata() != null ? documentEntry.metadata() : Map.of()
                )).toList();
        int chunks = ingestionService.ingestDocuments(docs);
        return ResponseEntity.ok(Map.of("chunksIngested", chunks));

    }

}
