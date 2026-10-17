package com.zyf.zhaiagent.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * LoveApp.doChatWithMcp 在调用 DashScope 模型时，模型可能需要结合 MCP 工具返回的结果来组织最终答案，这个过程耗时超过了客户端默认的容忍上限。
 *
 * 根据社区中大量 Spring AI Alibaba 用户的反馈，DashScope 在处理复杂请求或生成较长内容时，响应时间达到 60~180 秒甚至更久是很常见的。而 Spring AI
 * Alibaba 底层的默认读取超时通常只有 10~30 秒左右。你的测试在 20 秒左右就触发重试并最终失败，正好吻合这个时间窗口。
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return builder -> {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(10_000); // 10秒
            factory.setReadTimeout(300_000);   // 300秒（5分钟）
            builder.requestFactory(factory);
        };
    }
}