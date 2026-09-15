package com.zyf.zhaiagent.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration//告诉 Spring：这是一个配置类，启动时请扫描并处理它。它本身是 @Component 的"加强版"，所以会被 @ComponentScan（@SpringBootApplication
// 自带）扫到。被扫到之后，Spring 会解析这个类上的其他注解
@MapperScan(basePackages = "com.zyf.zhaiagent.mapper.mysql",//扫描指定包下的所有接口，为每个接口动态生成一个代理实现类（Mapper 代理对象），并注册成 Spring Bean
            sqlSessionTemplateRef = "mysqlSqlSessionTemplate")
public class MysqlMapperConfig {}