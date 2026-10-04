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

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        String message="我在镇江京口区，请帮我找到5公里内合适的玩耍地点";
        String answer = loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
