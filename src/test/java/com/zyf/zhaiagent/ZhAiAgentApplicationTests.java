package com.zyf.zhaiagent;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootTest
@EnableCaching                                        // @Cacheable 需要
@MapperScan("com.zyf.zhaiagent.mapper")               // 扫所有 Mapper
class ZhAiAgentApplicationTests {

    @Test
    void contextLoads() {
    }

}
