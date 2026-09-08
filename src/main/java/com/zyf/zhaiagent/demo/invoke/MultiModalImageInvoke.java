package com.zyf.zhaiagent.demo.invoke;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;

import java.util.Arrays;
import java.util.Collections;

/**
 * 多模态图片理解 - 让AI解释图片
 * 参考 SdkAilnvoke 的风格
 */
public class MultiModalImageInvoke {

    static {
        // 设置工作空间地址（你的 workspace-id）
        Constants.baseHttpApiUrl = "https://ws-x9vx8gdgkrgejywt.cn-beijing.maas.aliyuncs.com/api/v1";
    }

    /**
     * 通过图片URL让AI解释图片
     */
    public static MultiModalConversationResult explainImageByUrl() 
            throws ApiException, NoApiKeyException, InputRequiredException, UploadFileException {
        
        MultiModalConversation conv = new MultiModalConversation();

        // 1. System 消息：定义AI角色
        MultiModalMessage systemMsg = MultiModalMessage.builder()
                .role(Role.SYSTEM.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("text", "你是一个专业的图像分析助手，能够准确描述图片内容并回答用户问题。")
                ))
                .build();

        // 2. User 消息：包含图片URL + 文本问题
        MultiModalMessage userMsg = MultiModalMessage.builder()
                .role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", "https://pic4.zhimg.com/v2-6d94de2bc14e85082304ed19f81500f6_r.jpg?source=1940ef5c"), // 替换为你的图片URL
                        Collections.singletonMap("text", "请描述这张图片的内容，包括物体、颜色、场景等")
                ))
                .build();

        // 3. 构建请求参数
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(TestApiKey.API_KEY)  // 复用你的 API Key
                .model("qwen-vl-plus")        // 多模态模型
                .messages(Arrays.asList(systemMsg, userMsg))
                .build();

        return conv.call(param);
    }

    public static void main(String[] args) {
        try {
            System.out.println("========== 多模态图片分析 ==========");
            
            // 方式1：使用图片URL
            MultiModalConversationResult result = explainImageByUrl();
            System.out.println("【URL方式】分析结果:");
            System.out.println(JsonUtils.toJson(result));

            // 提取文本内容
            String textContent = result.getOutput()
                    .getChoices().get(0)
                    .getMessage().getContent().get(0)
                    .get("text").toString();
            System.out.println("\n【提取的文本内容】");
            System.out.println(textContent);

        } catch (ApiException | NoApiKeyException | InputRequiredException | UploadFileException e) {
            System.err.println("图片分析失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.exit(0);
    }
}









