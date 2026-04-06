package com.boghdady.springaidemo.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostRetrievalService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    /**
     * Context Compression with pre-retrieved documents
     */
    public List<Document> compressDocuments(String question, List<Document> retrievedDocs) {

        List<Document> compressedDocs = new ArrayList<>();

        for (Document doc : retrievedDocs) {
            String compressed = chatClient.prompt()
                    .user("""
                          Given the question below, extract ONLY the sentences from the
                          passage that are directly relevant to answering the question.
                          If no sentences are relevant, respond with: NOT_RELEVANT

                          Question: %s

                          Passage: %s

                          Relevant sentences:
                          """.formatted(question, doc.getText()))
                    .call()
                    .content();

            // Only include if not marked irrelevant
            if (!compressed.contains("NOT_RELEVANT") && !compressed.isBlank()) {
                compressedDocs.add(Document.builder()
                        .text(compressed)
                        .metadata(doc.getMetadata())
                        .build());
            }
        }

        log.info("Compressed {} documents to {} relevant chunks", retrievedDocs.size(), compressedDocs.size());
        return compressedDocs;
    }

    /**
     * LLM-Based Reranking with pre-retrieved documents
     */
    public List<Document> rerankDocuments(String question, List<Document> candidates, int topK) {
        List<ScoredDocument> scored = new ArrayList<>();

        for (Document doc : candidates) {
            String scoreResponse = chatClient.prompt()
                    .user("""
                          Score how relevant this passage is for answering the question.
                          Respond with ONLY a number from 1-10.

                          Question: %s
                          Passage: %s
                          Score:
                          """.formatted(question, doc.getText()))
                    .call()
                    .content();

            try {
                int score = Integer.parseInt(scoreResponse.trim());
                scored.add(new ScoredDocument(doc, score));
            } catch (NumberFormatException e) {
                scored.add(new ScoredDocument(doc, 5)); // Default score
            }
        }

        // Sort by score descending, take topK
        List<Document> reranked = scored.stream()
                .sorted((a, b) -> Integer.compare(b.score(), a.score()))
                .limit(topK)
                .map(ScoredDocument::document)
                .toList();

        log.info("Reranked {} documents, selected top {}", candidates.size(), reranked.size());
        return reranked;
    }

    /**
     * Long Context Reorder with pre-retrieved documents
     */
    public List<Document> reorderDocuments(List<Document> docs) {
        if (docs.isEmpty()) {
            return docs;
        }

        // Strategy: put most relevant first AND last, less relevant in the middle
        List<Document> reordered = new ArrayList<>();
        if (docs.size() > 2) {
            reordered.add(docs.get(0));                         // Most relevant: first
            reordered.addAll(docs.subList(2, docs.size()));     // Less relevant: middle
            reordered.add(docs.get(1));                         // Second most relevant: last
        } else {
            reordered = docs;
        }

        log.info("Reordered {} documents to prevent lost-in-the-middle", docs.size());
        return reordered;
    }

    private record ScoredDocument(Document document, int score) {}

}
