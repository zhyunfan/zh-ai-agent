package com.zyf.zhaiagent.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    /* ================= MySQL 数据源（敏感词库） ================= */
    //这个类的作用：手动创建 MySQL 数据源，并把它包装成 MyBatis 能用的 SqlSessionTemplate，最后再配一个事务管理器。
    //因为 Spring Boot 默认只自动配一个数据源，多数据源时必须手写。
    //postgre同上
    //mysqlDataSource              （连接池，连 MySQL）
    //      │ 被引用
    //      ▼
    //mysqlSqlSessionFactory       （MyBatis 的工厂，绑定 DataSource）
    //      │ 被引用
    //      ▼
    //mysqlSqlSessionTemplate      （MyBatis 执行 SQL 的模板，绑定 Factory）
    //      │ 被引用
    //      ▼
    //（MysqlMapperConfig 里的 @MapperScan sqlSessionTemplateRef 指向它）
    //
    //mysqlTransactionManager      （事务管理器，也绑定 DataSource）

    @Bean("mysqlDataSource")//把这个 HikariDataSource 注册成 Spring 容器里名字叫 mysqlDataSource 的 Bean。
//    其他 Bean 通过 @Qualifier("mysqlDataSource") 就能拿到它。
    @ConfigurationProperties(prefix = "spring.datasource.mysql")//把 yaml 里 datasource.mysql.* 下的所有属性，自动塞进刚 new 出来的 HikariDataSource 对象里
    public DataSource mysqlDataSource() {
        return new HikariDataSource();//创建一个 HikariCP 连接池对象
    }

    @Bean("mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(//@Qualifier("bean名") 精确注入。
            @Qualifier("mysqlDataSource") DataSource ds) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(ds);//把 MyBatis 绑定到这个数据源，这个 Factory 产出的所有 SQL 都走 MySQL
        // 如果敏感词 mapper 有 XML，指向对应位置；没有可省略
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:mapper/mysql/*.xml"));
        return bean.getObject();
    }

    @Bean("mysqlSqlSessionTemplate")
    public SqlSessionTemplate mysqlSqlSessionTemplate(
            @Qualifier("mysqlSqlSessionFactory") SqlSessionFactory factory) {
        return new SqlSessionTemplate(factory);
    }

    @Bean("mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlDataSource") DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    /* ================= PostgreSQL 数据源（PgVector 向量库） ================= */

    @Primary   // 标记为默认数据源，没指定 qualifier 的地方用它
    @Bean("postgresDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.postgres")
    public DataSource postgresDataSource() {
        return new HikariDataSource();
    }

    @Primary
    @Bean("postgresSqlSessionFactory")
    public SqlSessionFactory postgresSqlSessionFactory(
            @Qualifier("postgresDataSource") DataSource ds) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(ds);
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:mapper/postgres/*.xml"));
        return bean.getObject();
    }

    @Primary
    @Bean("postgresSqlSessionTemplate")
    public SqlSessionTemplate postgresSqlSessionTemplate(
            @Qualifier("postgresSqlSessionFactory") SqlSessionFactory factory) {
        return new SqlSessionTemplate(factory);
    }

    @Primary
    @Bean("postgresTransactionManager")
    public PlatformTransactionManager postgresTransactionManager(
            @Qualifier("postgresDataSource") DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }
}