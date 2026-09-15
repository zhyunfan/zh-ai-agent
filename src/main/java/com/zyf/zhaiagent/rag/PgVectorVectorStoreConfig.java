package com.zyf.zhaiagent.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
public class PgVectorVectorStoreConfig {

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1024)                    // ← 建表时列类型 vector(1024) 来自这里
                .distanceType(COSINE_DISTANCE)       // ← 建索引时的距离类型
                .indexType(HNSW)                     // ← 建索引类型
                .initializeSchema(true)              // ← 是否允许自动建表
                .schemaName("public")                // ← 建在哪个 schema
                .vectorTableName("vector_store")     // ← 表名
                .maxDocumentBatchSize(10000)
                .build();
        return vectorStore;
    }
}
















