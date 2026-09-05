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
//public class SpringAiAiInvoke implements CommandLineRunner {
//
//    //@Resource 是Java中一个非常实用的依赖注入（Dependency Injection）注解，它的核心作用就是让容
//    // 器（比如Spring）帮你自动创建一个对象，并把它“注入”到你当前的代码中，省去你自己new对象的麻烦。
//    @Resource
//    private ChatModel dashscopeChatModel;//阿里云灵积的，名称一定要是dashscopeChatModel，因为点击左侧的bin图标就会跳转到该名称的chatmodel上
//    //spring ai好像是先通过名称引入某种chatmodel的（有多种类型的chatmodel），如果名称找不到就通过类型找
//
//    @Override
//    public void run(String... args) throws Exception {
//        AssistantMessage assistantMessage=dashscopeChatModel.call(new Prompt("hello,我是一个大学生"))
//                .getResult()
//                .getOutput();
//        System.out.println(assistantMessage.getText());
//    }
//}
