package com.boghdady.springaidemo.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreRetrievalService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    // ── TECHNIQUE 1: Query Rewriting ─────────────────────────────────────────
    /**
     * Uses the LLM to rewrite the user's query into a better search query.
     * The rewritten query is then used for vector similarity search.
     */
    public String askWithQueryRewriting(String originalQuestion) {

        // Step 1: Rewrite the query using LLM
        String rewrittenQuery = chatClient.prompt()
                .user("""
                      Rewrite the following user question to be a clear, specific,
                      search-optimized query for a company knowledge base.
                      Return ONLY the rewritten query, no explanation.

                      Original question: %s
                      """.formatted(originalQuestion))
                .call()
                .content();

        // Step 2: Search with the rewritten (better) query
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(rewrittenQuery)
                        .topK(4)
                        .build()
        );

        // Step 3: Generate answer
        return generateAnswer(originalQuestion, docs);
    }

    // ── TECHNIQUE 2: HyDE ────────────────────────────────────────────────────
    /**
     * HyDE: Hypothetical Document Embeddings.
     * Generates a hypothetical answer and uses -> it <- as the search query.
     * More effective than embedding the question directly.
     */
    public String askWithHyDE(String question) {

        // Step 1: Ask LLM to generate a hypothetical answer
        // (It will hallucinate, but that's okay — we use it for search,
        // not as the answer)
        String hypotheticalAnswer = chatClient.prompt()
                .user("""
                      Write a hypothetical answer to the following question.
                      The answer may not be accurate — we are using it to find similar real documents.
                      Be concise (2-3 sentences).

                      Question: %s
                      Hypothetical Answer:
                      """.formatted(question))
                .call()
                .content();


        // Step 2: Embed and search using the hypothetical answer
        // (hypothetical answer is semantically closer to real documents than the question)
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(hypotheticalAnswer)
                        .topK(4)
                        .build()
        );

        // Step 3: Generate real answer using retrieved real documents
        return generateAnswer(question, docs);
    }

    // ── TECHNIQUE 3: Multi-Query ─────────────────────────────────────────────
    /**
     * Multi-Query: generate multiple query variants, retrieve for each, deduplicate.
     * Achieves higher recall than single-query retrieval.
     */
    public String askWithMultiQuery(String question) {

        // Step 1: Generate 3 query variants
        String variantsResponse = chatClient.prompt()
                .user("""
                      Generate 3 different search queries for a company knowledge base
                      to answer the following question.
                      Return ONLY the queries, one per line, no numbering or explanation.

                      Question: %s
                      """.formatted(question))
                .call()
                .content();

        // Parse the response into a list of queries
        List<String> queries = Arrays.stream(variantsResponse.split("\n"))
                .map(String::trim)
                .filter(q -> !q.isBlank())
                .limit(3)
                .toList();

        // Step 2: Retrieve for each query, collect all unique documents
        Map<String, Document> uniqueDocs = new LinkedHashMap<>();
        for (String query : queries) {
            vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(3).build())
                    .forEach(doc -> uniqueDocs.put(doc.getId(), doc));
        }

        // Convert the map values back to a list of unique documents
        List<Document> combinedDocs = new ArrayList<>(uniqueDocs.values());

        // Step 3: Generate answer from combined documents
        return generateAnswer(question, combinedDocs);
    }


    private String generateAnswer(String question, List<Document> docs) {
        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        return chatClient.prompt()
                .user("""
                      Answer using ONLY the context below.
                      Context: %s
                      Question: %s
                      """.formatted(context, question))
                .call()
                .content();
    }

}
