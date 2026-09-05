package com.zyf.zhaiagent.app;

import com.zyf.zhaiagent.advisor.MyLoggerAdvisor;
import com.zyf.zhaiagent.advisor.ReReadingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopeChatModel) {
        // 初始化基于内存的对话记忆
        // 1. 创建内存仓储
        InMemoryChatMemoryRepository memoryRepository = new InMemoryChatMemoryRepository();

        // 2. 使用 Builder 构建 ChatMemory，并绑定仓储
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(memoryRepository) // 绑定内存仓储
                .maxMessages(10) // 可选：限制对话历史条数，避免上下文过长
                .build();

        //初始化客户端对象
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(//之后每次调用chatClient的对话都会用该拦截器，对所有请求生效
                        MessageChatMemoryAdvisor.builder(chatMemory).build() ,// 推荐使用 builder()
                        new MyLoggerAdvisor(),//自定义日志Advisor，可按需开启
                        new ReReadingAdvisor()//自定义推理增强Advisor,可按需开启，但是token数量翻倍了
                )
                .build();
//        chatClient.prompt().advisors();对单个请求生效
    }

    //ai基础对话（支持多轮对话记忆）
    // 返回当前ai输出的内容 message:用户输入的消息，chatId:当前对话id
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)//当前id上下文
                        )//历史内容条数
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

}
