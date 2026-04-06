package com.boghdady.springaidemo.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;

    /**
     * Basic similarity search — retrieves top-K documents similar to the query
     */
    public List<Document> retrieve(String query, int topK, double threshold) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(threshold)
                        .build()
        );
        log.debug("Retrieved {} documents for query: {}", results.size(), query);
        return results;
    }

    /**
     * Filtered search — retrieves documents matching BOTH semantic similarity
     * AND a metadata filter.
     *
     * Example: only search documents with category == 'policy'
     *
     * Spring AI FilterExpression supports:
     *   ==, !=, <, <=, >, >=
     *   AND, OR, NOT
     *   IN, NIN
     */
    public List<Document> retrieveWithFilter(String query, String category, int topK) {

        // Build filter
        var filter = new FilterExpressionBuilder()
                .eq("category", category)
                .build();

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(0.6)
                        .filterExpression(filter)
                        .build()
        );
        log.debug("Filtered retrieval: {} docs for category={}", results.size(), category);
        return results;
    }

    /**
     * Multi-filter search — combine multiple metadata criteria.
     * Example: category == 'policy' AND department == 'engineering'
     */
    public List<Document> retrieveWithMultiFilter(String query, String category, String dept, int topK) {

        var filters = new FilterExpressionBuilder();
        var filter = filters.and(
                filters.eq("category", category),
                filters.eq("department", dept)
        ).build();

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(0.6)
                        .filterExpression(filter)
                        .build()
        );
    }



}
