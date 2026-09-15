package com.zyf.zhaiagent.app;

import com.zyf.zhaiagent.advisor.MyLoggerAdvisor;
import com.zyf.zhaiagent.advisor.SensitiveWordAdvisor;
import com.zyf.zhaiagent.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopeChatModel) {
        //初始化基于文件的对话记忆
        String fileDir=System.getProperty("user.dir")+"/tmp/chat-memory";
        ChatMemory chatMemory=new FileBasedChatMemory(fileDir);

//        // 初始化基于内存的对话记忆
//        // 1. 创建内存仓储
//        InMemoryChatMemoryRepository memoryRepository = new InMemoryChatMemoryRepository();
//
//        // 2. 使用 Builder 构建 ChatMemory，并绑定仓储
//        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .chatMemoryRepository(memoryRepository) // 绑定内存仓储
//                .maxMessages(10) // 可选：限制对话历史条数，避免上下文过长
//                .build();

        //初始化客户端对象
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(//之后每次调用chatClient的对话都会用该拦截器，对所有请求生效
                        MessageChatMemoryAdvisor.builder(chatMemory).build() ,// 推荐使用 builder()
                        new MyLoggerAdvisor()//自定义日志Advisor，可按需开启
//                       , new ReReadingAdvisor()//自定义推理增强Advisor,可按需开启，但是token数量翻倍了
                        ,new SensitiveWordAdvisor()
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

    record LoveReport(String title, List<String> suggestions) {
    }

    /**
     * AI恋爱报告功能（实现结构化输出）
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    //ai恋爱知识库问答功能
    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    /**
     * 和RAG知识库进行对话
     * @param message
     * @param chatId
     * @return
     *
     *loveAppVectorStore过程：
     * 1. chatClient.prompt().user(message)   → 构造请求
     * 2. .advisors(...)                       → 注册 Advisor（不执行）
     * 3. .advisors(qaAdvisor)                 → 注册 RAG Advisor（不执行）
     * 4. .call()                              → 真正触发执行
     *    ↓
     *    Advisor 链开始执行（后注册的先执行）：
     *    ↓
     *    QuestionAnswerAdvisor.adviseCall()
     *    ├─ 取出用户问题 message
     *    ├─ 调用 EmbeddingModel 把 message 向量化   ← 向量化在这里！
     *    ├─ 用向量去 loveAppVectorStore 做相似度检索
     *    ├─ 把检索到的文档拼进 prompt
     *    └─ 生成新请求，传给下一个 Advisor
     *    ↓
     *    MyLoggerAdvisor.adviseCall()          ← 此时看到的是已拼接 RAG 的请求
     *    ├─ logRequest(chatClientRequest)
     *    ├─ callAdvisorChain.nextCall(...)     → 调用大模型
     *    └─ logResponse(chatClientResponse)
     *    ↓
     *    大模型返回结果
     * 5. .chatResponse()                      → 拿到最终响应
     */
    public String doChatWithRag(String message,String chatId){
        //之前写的 new QuestionAnswerAdvisor(loveAppVectorStore) 是在直接调用构造器，而你只传了 1 个参数，编译器自然去找匹配的构造器，结果发现需要 5 个参数的版本，于是报错。
        //而 builder() 方法是静态工厂方法，它内部会帮你把那些参数都准备好，所以你只需要传 VectorStore 就行。
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(loveAppVectorStore)//rag需要存储，所以用到VectorStore
                .searchRequest(SearchRequest.builder().topK(4).similarityThreshold(0.5).build())
                .build();
        ChatResponse chatResponse=chatClient
                .prompt()
                .user(message)
                .advisors(spec->spec.param(ChatMemory.CONVERSATION_ID,chatId))
                //开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                //应用RAG知识库问答:
                //用户问题 message 先被向量化
                //去 loveAppVectorStore 里做相似度检索，找出最相关的文档片段
                //把检索到的内容拼进 prompt，作为上下文一起发给大模型
                //大模型基于这些资料回答，而不是凭空瞎编 → 降低幻觉
//                .advisors(qaAdvisor)//这里才是把文本转换为向量
                //应用RAG检索增强服务（基于云知识库服务）
                .advisors(loveAppRagCloudAdvisor)
                .call()
                .chatResponse();//返回完整的 ChatResponse 对象（包含回复内容、元数据、token 用量等）
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }
}
