package com.eric.system.multipledatabase;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(basePackages = MasterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "masterSqlSessionFactory")
@Slf4j
public class MasterDataSourceConfig {

    // 精确到 master 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.eric.dao.master";
    static final String MAPPER_LOCATION = "classpath*:/mybatis/master/*Mapper.xml";

    @Value("${spring.datasource.master.url}")
    private String url;

    @Value("${spring.datasource.master.username}")
    private String user;

    @Value("${spring.datasource.master.password}")
    private String password;

    @Value("${spring.datasource.master.driverClassName}")
    private String driverClass;

    @Value("${spring.datasource.master.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.master.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.master.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.master.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.master.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.master.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.master.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.master.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.master.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.master.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.master.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.master.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.master.filters}")
    private String filters;

    @Value("${spring.datasource.master.connectionProperties}")
    private String connectionProperties;

    @Value("${spring.datasource.master.useGlobalDataSourceStat}")
    private boolean useGlobalDataSourceStat;

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            log.error("masterDataSource druid configuration initialization filter", e);
        }
        dataSource.setConnectionProperties(connectionProperties);
        dataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
        return dataSource;
    }

    @Bean(name = "masterTransactionManager")
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }

    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(MasterDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
