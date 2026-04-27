package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.model.RagResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

// Checks that LLM response stays within retrieved context
@Service

public class EvaluatedRagService {

    private static final Logger log = LoggerFactory.getLogger(EvaluatedRagService.class);

    private final ChatClient ragClient;
    private final VectorStore vectorStore;
    private final RelevancyEvaluator evaluator;

    public EvaluatedRagService(@Qualifier("ragClient")ChatClient ragClient,
                               VectorStore vectorStore,
                               RelevancyEvaluator evaluator) {

        this.ragClient = ragClient;
        this.vectorStore = vectorStore;
        this.evaluator = evaluator;
    }

    public RagResult ask(String question) {
        String response = ragClient.prompt(question).call().content();

        // get related docs for evaluation
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(3)
                        .build());

        // Evaluate grounding
        EvaluationResponse eval = evaluator.evaluate(new EvaluationRequest(question, docs, response));

        log.info("[RAG EVAL] score={} pass={} docs={} q='{}'",
                eval.getScore(), eval.isPass(), docs.size(), question);

        return new RagResult(response, eval.getScore(), eval.isPass(), docs.size(), eval.getFeedback());
    }

}
