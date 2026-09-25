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
 *          @Component    @Bean
 * 作用位置	类上	            方法上
 *
 * @Resource 是“取”，不是“造”。 它只负责从 Spring 容器里拿现成的 Bean，不负责创建。创建的事，要么交给 @Component 自动扫描，要么交给 @Bean 手动定义。
 * @Component：Spring 扫描后自动 new
 * @Bean：你在 @Configuration 方法里手动 new，交给 Spring 管
 * 至于怎么new:
 * 	                @Component	                    @Bean
 * 谁负责 new	    Spring 自动 new	                你自己手动 new
 * 你写什么	        只加注解，不写 new	                必须写 return new Xxx()
 * Spring 做什么	    扫描到类 → 反射创建实例 → 放进容器	调用你的方法 → 拿到返回值 → 放进容器
 *-------------------------------------------------------------------------
 * @Configuration 类里定义 Bean
 * @Bean 注解告诉 Spring：
 * 把 loveAppVectorStore() 方法的!!返回值!!，作为一个 Bean 注册到容器里，Bean 的名字就是方法名 loveAppVectorStore。
 * 业务类里通过 @Resource 注入：
 * @Resource
 * private LoveAppDocumentLoader loveAppDocumentLoader;
 * @Resource 告诉 Spring：
 * 帮我从容器里找一个叫 loveAppDocumentLoader 的 Bean，赋给这个字段。
 */
@Configuration
public class LoveAppVectorStoreConfig {
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) throws IOException {
        //有了嵌入大模型，就进行简易的基于内存的vectorstore存储
        //根据 Spring AI Alibaba 官方文档，dashscopeEmbeddingModel 的默认模型是 text-embedding-v2，1536 是 OpenAI text-embedding-3-small 模型的默认输出维度
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        //加载文档
        List<Document> documentList=loveAppDocumentLoader.loadMarkdowns();
//        //自主切分文档，不推荐，因为切的不好
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documentList);
        //自动补充关键词元信息
        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documentList);
        //存储位置	JVM 堆内存（一个 Map 结构）
        simpleVectorStore.add(enrichDocuments);
        return simpleVectorStore;
    }
}






