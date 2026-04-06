package com.boghdady.springaidemo.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class VectorStoreConfig {

    @Value("${spring.ai.vectorStore.qdrant.host}")
    private String host;

    @Value("${spring.ai.vectorStore.qdrant.port}")
    private int port;

    @Value("${spring.ai.vectorStore.qdrant.collection-name}")
    private String collection;

    @Value("${spring.ai.vectorStore.qdrant.use-tls}")
    private boolean useTls;

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(QdrantGrpcClient.newBuilder(host, port, useTls).build());
    }

    @Bean
    public VectorStore vectorStore(QdrantClient client, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(client, embeddingModel)
                .initializeSchema(true)
                .collectionName(collection)
                .build();
    }


}
