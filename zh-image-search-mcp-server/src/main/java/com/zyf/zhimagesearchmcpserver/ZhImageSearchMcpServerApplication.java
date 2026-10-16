package com.zyf.zhimagesearchmcpserver;

import com.zyf.zhimagesearchmcpserver.tools.ImageSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ZhImageSearchMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhImageSearchMcpServerApplication.class, args);
    }

    //客户端zh-ai-agent怎么通过本地模式去调用mcp服务的：客户端先找到mcp的配置，找到启动mcp的命令，开一个子进程
    //去运行这个服务，要把本地mcp服务打个jar包,客户端调用时就是运行这个jar包
    @Bean
    public ToolCallbackProvider imageSearchTools(ImageSearchTool imageSearchTool){
        return MethodToolCallbackProvider.builder()
                .toolObjects(imageSearchTool)
                .build();
    }
}
