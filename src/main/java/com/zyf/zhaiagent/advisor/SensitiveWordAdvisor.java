package com.zyf.zhaiagent.advisor;

import com.zyf.zhaiagent.service.SensitiveWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class SensitiveWordAdvisor implements CallAdvisor, StreamAdvisor {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 使用AtomicReference支持动态更新
     */
    private final AtomicReference<List<String>> sensitiveWordsRef = new AtomicReference<>();

    // 无参构造（供 new 使用）
    public SensitiveWordAdvisor() {
    }

    // 有参构造（供 Spring 使用）
    public SensitiveWordAdvisor(SensitiveWordService sensitiveWordService) {
        this.sensitiveWordService = sensitiveWordService;
    }

    @PostConstruct
    public void init() {
        // 初始化时加载
        refreshSensitiveWords();
    }

    /**
     * 刷新违禁词列表
     */
    public void refreshSensitiveWords() {
        List<String> words = sensitiveWordService.loadSensitiveWords();
        sensitiveWordsRef.set(words);
        log.info("违禁词列表已更新，共 {} 个词", words.size());
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        ChatClientRequest filteredRequest = filterRequest(request);
        ChatClientResponse response = chain.nextCall(filteredRequest);
        return filterResponse(response);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        ChatClientRequest filteredRequest = filterRequest(request);
        Flux<ChatClientResponse> responses = chain.nextStream(filteredRequest);
        return new ChatClientMessageAggregator()
                .aggregateChatClientResponse(responses, this::filterResponse);
    }

    protected ChatClientRequest filterRequest(ChatClientRequest request) {
        List<String> words = sensitiveWordsRef.get();
        if (words == null || words.isEmpty()) {
            return request;
        }

        String originalText = request.prompt().getUserMessage().getText();
        String filteredText = filterSensitiveWords(originalText, words);

        if (!filteredText.equals(originalText)) {
            log.warn("检测到违禁词，已过滤");
        }

        return request.mutate()
                .prompt(request.prompt().augmentUserMessage(filteredText))
                .build();
    }

    protected ChatClientResponse filterResponse(ChatClientResponse response) {
        List<String> words = sensitiveWordsRef.get();
        if (words == null || words.isEmpty()) {
            return response;
        }

        // 1. 读取原有 context
        Map<String, Object> oldContext = response.context();

        // 2. 获取原始文本
        String originalText = (String) oldContext.get("userText");
        String filteredText = filterSensitiveWords(originalText,words);

        // 3. 创建一个新的 Map，并复制所有旧数据
        Map<String, Object> newContext = new HashMap<>(oldContext);
        // 4. 更新过滤后的文本
        newContext.put("userText", filteredText);

        // 5. 使用 mutate() 构建新响应
        return response.mutate()
                .context(newContext)  // 直接传入新的 Map
                .build();
    }

    protected String filterSensitiveWords(String text, List<String> words) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String result = text;
        for (String word : words) {
            result = result.replace(word, "***");
        }
        return result;
    }

    // 在 SensitiveWordAdvisor 中添加
    public boolean containsSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        List<String> words = sensitiveWordsRef.get();
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}