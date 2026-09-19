//package com.zyf.zhaiagent.rag;
//
//import jakarta.annotation.Resource;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
//import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;
//

//阿里云服务欠费了哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈
//@Configuration
//public class PgVectorVectorStoreConfig {
//
//    @Resource
//    private LoveAppDocumentLoader loveAppDocumentLoader;
//
//
//    //你在 @Bean 方法里调用了 vectorStore.add()，但 PgVectorStore.afterPropertiesSet()（建表逻辑）还没执行。
//    //afterPropertiesSet() 是 Spring 在 @Bean 方法返回之后 才调用的。你在方法体里调 add()，此时 afterPropertiesSet() 尚未运行，表还没建，INSERT 自然失败。
//    @Bean
//    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) throws IOException {
//        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
//                .dimensions(1024)                    // ← 建表时列类型 vector(1024) 来自这里
//                .distanceType(COSINE_DISTANCE)       // ← 建索引时的距离类型
//                .indexType(HNSW)                     // ← 建索引类型
//                .initializeSchema(true)              // ← 是否允许自动建表
//                .schemaName("public")                // ← 建在哪个 schema
//                .vectorTableName("vector_store")     // ← 表名
//                .maxDocumentBatchSize(10000)
//                .build();
//        //加载文档
//        List<Document> documents=loveAppDocumentLoader.loadMarkdowns();
//        int batchSize = 10;
//        for (int i = 0; i < documents.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, documents.size());
//            vectorStore.add(documents.subList(i, end));   // 每次 10 条，DashScope（阿里云百炼）的 Embedding 接口一次最多只能处理 10 条文本
//        }
//        return vectorStore;
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
