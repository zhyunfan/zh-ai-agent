package com.zyf.zhaiagent.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.zyf.zhaiagent.mapper.postgre",
            sqlSessionTemplateRef = "postgresSqlSessionTemplate")
public class PostgresMapperConfig {}