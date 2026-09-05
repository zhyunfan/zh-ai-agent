//package com.zyf.zhaiagent.demo.invoke;
//
//import jakarta.annotation.Resource;
//import org.springframework.ai.chat.messages.AssistantMessage;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
///**
// * Spring AI 框架调用AI大模型
// *
// * 接下来怎么启动一个spring ai项目来单次测试ai是否调用成功，通过CommandLineRunner实现单次执行的方法
// * 当项目启动时会扫描component注解的bean，发现实现了CommandLineRunner就执行run方法，同时自动注入依赖dashscopeChatModel（@Resource）调用ai大模型了
// */
//@Component
//public class OllamaAiInvoke implements CommandLineRunner {
//
//    @Resource
//    private ChatModel ollamaChatModel;
//
//    @Override
//    public void run(String... args) throws Exception {
//        AssistantMessage assistantMessage=ollamaChatModel.call(new Prompt("hello,3*3=?"))
//                .getResult()
//                .getOutput();
//        System.out.println(assistantMessage.getText());
//    }
//}
