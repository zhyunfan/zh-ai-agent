package com.zyf.zhaiagent.app;

import com.zyf.zhaiagent.advisor.SensitiveWordAdvisor;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Autowired
    private SensitiveWordAdvisor sensitiveWordAdvisor;  // 注入 Advisor

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是女程序员zyf";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);//判断answer不为空
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是程序员鱼皮，我想让另一半（编程导航）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    /**
     * 测试多个违禁词混合
     */
    @Test
    void doChatWithReport_MultipleSensitiveWords() {
        String chatId = UUID.randomUUID().toString();
        String[] sensitiveMessages = {
                "我要去赌博",
                "这里有毒品",
                "你这个色情狂"
        };

        for (String message : sensitiveMessages) {
            LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
            Assertions.assertNotNull(loveReport);

            // 1. 检查 title
            String title = loveReport.title();
            if (title != null) {
                Assertions.assertFalse(sensitiveWordAdvisor.containsSensitiveWords(title),
                        "消息 '" + message + "' 的 title 包含违禁词: " + title);
            }

            // 2. 检查 suggestions 列表
            List<String> suggestions = loveReport.suggestions();
            if (suggestions != null) {
                for (String suggestion : suggestions) {
                    Assertions.assertFalse(sensitiveWordAdvisor.containsSensitiveWords(suggestion),
                            "消息 '" + message + "' 的 suggestion 包含违禁词: " + suggestion);
                }
            }
        }
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
