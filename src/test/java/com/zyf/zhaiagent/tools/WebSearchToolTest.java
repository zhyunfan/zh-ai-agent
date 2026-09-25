package com.zyf.zhaiagent.tools;

import io.a2a.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

//加载配置文件
@SpringBootTest//之前遇到的“PostgreSQL 连接失败”“阿里云欠费”这些错误，都是在这个阶段暴露出来的——因为 @SpringBootTest 会真实地初始化所有 Bean。
class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Test
    void searchWeb() {
        WebSearchTool webSearchTool=new WebSearchTool(searchApiKey);
        String query="codefather.cn是什么";
        String result = webSearchTool.searchWeb(query);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }
}