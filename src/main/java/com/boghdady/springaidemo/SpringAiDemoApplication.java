package com.boghdady.springaidemo;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class SpringAiDemoApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringAiDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadDocuments(VectorStore vectorStore) {
        return args -> {
            List<Document> docs = List.of(
                    new Document(
                            "Spring Boot auto-configuration automatically configures " +
                                    "your Spring application based on the JAR dependencies you " +
                                    "have added. For example, if HSQLDB is on your classpath, " +
                                    "and you have not configured any database connection beans, " +
                                    "then Spring Boot auto-configures an in-memory database.",
                            Map.of("source", "spring-boot-docs",
                                    "topic", "auto-configuration")
                    ),
                    new Document(
                            "Spring AI ChatClient is a high-level fluent API for " +
                                    "interacting with AI models. It supports prompt building, " +
                                    "default system messages, tool callbacks, advisors, and " +
                                    "streaming. ChatClient wraps a ChatModel and provides " +
                                    "a builder pattern for configuration.",
                            Map.of("source", "spring-ai-docs",
                                    "topic", "chat-client")
                    ),
                    new Document(
                            "The @Transactional annotation in Spring manages database " +
                                    "transactions declaratively. It supports propagation levels: " +
                                    "REQUIRED (default), REQUIRES_NEW, NESTED, SUPPORTS, " +
                                    "NOT_SUPPORTED, MANDATORY, and NEVER. Isolation levels " +
                                    "control concurrent access behavior.",
                            Map.of("source", "spring-framework-docs",
                                    "topic", "transactions")
                    ),
                    new Document(
                            "Spring Security filter chain processes HTTP requests " +
                                    "through a series of security filters. Key filters include " +
                                    "CsrfFilter, UsernamePasswordAuthenticationFilter, " +
                                    "BearerTokenAuthenticationFilter, and " +
                                    "AuthorizationFilter. Order matters.",
                            Map.of("source", "spring-security-docs",
                                    "topic", "security")
                    )
            );
            vectorStore.add(docs);
            log.info("Successfully loaded {} documents into vector store", docs.size());
        };
    }

}
