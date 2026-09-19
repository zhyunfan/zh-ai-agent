package com.zyf.zhaiagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 自定义检索增强过滤器（rag检索增强顾问）
 * 因为要让工厂创建实例，所以用工厂模式
 */
public class LoveAppRagCustomAdvisorFactory {
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore,String status){
        //过滤表达式
        //过滤特定状态的文档
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)//查询语法，使status=用户指定的status(参数)，status是文档里的字段名在元数据metadata中
                .build();
        //文档检索器
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()//向量数据库的文档检索器因为用向量数据库
                .vectorStore(vectorStore)
                .filterExpression(expression)
                .similarityThreshold(0.7)//相似度阈值
                .topK(3)//返回3个
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)//制定文档检索器,自定义文档的过滤条件
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())//文档上下文查询增强器
                .build();
    }
}
