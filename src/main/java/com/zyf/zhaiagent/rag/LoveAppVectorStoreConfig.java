package com.zyf.zhaiagent.rag;


import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * 恋爱大师向量数据库配置（初始化基于内存的向量数据库Bean）
 *
 * @Configuration 类里定义 Bean
 * @Bean 注解告诉 Spring：
 * 把 loveAppVectorStore() 方法的返回值，作为一个 Bean 注册到容器里，Bean 的名字就是方法名 loveAppVectorStore。
 * 业务类里通过 @Resource 注入：
 * @Resource
 * private VectorStore loveAppVectorStore;
 * @Resource 告诉 Spring：
 * 帮我从容器里找一个叫 loveAppVectorStore 的 Bean，赋给这个字段。
 */
@Configuration
public class LoveAppVectorStoreConfig {
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;
    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) throws IOException {
        //有了嵌入大模型，就进行简易的基于内存的vectorstore存储
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        //加载文档
        List<Document> documentList=loveAppDocumentLoader.loadMarkdowns();
        //存储位置	JVM 堆内存（一个 Map 结构）
        simpleVectorStore.add(documentList);
        return simpleVectorStore;
    }
}






