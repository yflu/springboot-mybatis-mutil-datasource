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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(basePackages = ClusterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "clusterSqlSessionFactory")
@Slf4j
public class ClusterDataSourceConfig {

    // 精确到 cluster 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.eric.dao.cluster";
    static final String MAPPER_LOCATION = "classpath*:/mybatis/cluster/*Mapper.xml";

    @Value("${spring.datasource.cluster.url}")
    private String url;

    @Value("${spring.datasource.cluster.username}")
    private String user;

    @Value("${spring.datasource.cluster.password}")
    private String password;

    @Value("${spring.datasource.cluster.driverClassName}")
    private String driverClass;

    @Value("${spring.datasource.cluster.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.cluster.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.cluster.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.cluster.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.cluster.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.cluster.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.cluster.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.cluster.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.cluster.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.cluster.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.cluster.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.cluster.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.cluster.filters}")
    private String filters;

    @Value("${spring.datasource.cluster.connectionProperties}")
    private String connectionProperties;

    @Value("${spring.datasource.cluster.useGlobalDataSourceStat}")
    private boolean useGlobalDataSourceStat;

    @Bean(name = "clusterDataSource")
    @Primary
    public DataSource clusterDataSource() {
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

    @Bean(name = "clusterTransactionManager")
    @Primary
    public DataSourceTransactionManager clusterTransactionManager() {
        return new DataSourceTransactionManager(clusterDataSource());
    }

    @Bean(name = "clusterSqlSessionFactory")
    @Primary
    public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("clusterDataSource") DataSource clusterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(ClusterDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
