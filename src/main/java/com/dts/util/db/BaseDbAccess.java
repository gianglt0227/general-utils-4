/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author GiangLT
 */
public abstract class BaseDbAccess {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected DataSource dataSource;
    protected Properties properties;

    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            dataSource = setupDataSource();
        }

        Connection conn = dataSource.getConnection();
//        if (conn != null && !conn.isClosed()) {
//            conn.setAutoCommit(false);
//        }
        return conn;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
        setupDataSource();
    }

    protected DataSource setupDataSource() {
        try {
            logger.info("Setting up datasource");
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(properties.getProperty("jdbc.jdbcUrl", "jdbc:oracle:thin:@192.168.6.248:1521:mca"));
            hikariConfig.setUsername(properties.getProperty("jdbc.user", "livescreen"));
            hikariConfig.setPassword(properties.getProperty("jdbc.password", "livescreen"));
            hikariConfig.setAutoCommit(Boolean.parseBoolean(properties.getProperty("jdbc.autoCommit", "false")));
            hikariConfig.setConnectionTimeout(Long.valueOf(properties.getProperty("jdbc.connectionTimeout", "30000")));
            hikariConfig.setIdleTimeout(Long.valueOf(properties.getProperty("jdbc.idleTimeout", "600000")));
            hikariConfig.setMaxLifetime(Long.valueOf(properties.getProperty("jdbc.maxLifetime", "600000")));
            hikariConfig.setMinimumIdle(Integer.valueOf(properties.getProperty("jdbc.minimumIdle", "3")));
            hikariConfig.setMaximumPoolSize(Integer.valueOf(properties.getProperty("jdbc.maximumPoolSize", "10")));

            String driverClassName = properties.getProperty("jdbc.driverClassName", "oracle.jdbc.OracleDriver");
            if (driverClassName.contains("mysql") || driverClassName.contains("mariadb")) {
                hikariConfig.addDataSourceProperty("cachePrepStmts", properties.getProperty("jdbc.mysql.cachePrepStmts", "true"));
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", properties.getProperty("jdbc.mysql.prepStmtCacheSize", "250"));
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getProperty("jdbc.mysql.prepStmtCacheSqlLimit", "2048"));
                hikariConfig.addDataSourceProperty("useServerPrepStmts", properties.getProperty("jdbc.mysql.useServerPrepStmts", "true"));
                hikariConfig.addDataSourceProperty("useLocalSessionState", properties.getProperty("jdbc.mysql.useLocalSessionState", "true"));
                hikariConfig.addDataSourceProperty("useLocalTransactionState", properties.getProperty("jdbc.mysql.useLocalTransactionState", "true"));
                hikariConfig.addDataSourceProperty("rewriteBatchedStatements", properties.getProperty("jdbc.mysql.rewriteBatchedStatements", "true"));
                hikariConfig.addDataSourceProperty("cacheResultSetMetadata", properties.getProperty("jdbc.mysql.cacheResultSetMetadata", "true"));
                hikariConfig.addDataSourceProperty("cacheServerConfiguration", properties.getProperty("jdbc.mysql.cacheServerConfiguration", "true"));
                hikariConfig.addDataSourceProperty("elideSetAutoCommits", properties.getProperty("jdbc.mysql.elideSetAutoCommits", "true"));
                hikariConfig.addDataSourceProperty("maintainTimeStats", properties.getProperty("jdbc.mysql.maintainTimeStats", "false"));
            } else {
                hikariConfig.setDriverClassName(driverClassName);
            }
            HikariDataSource ds = new HikariDataSource(hikariConfig);
            return ds;
        } catch (Exception ex) {
            logger.error("", ex);
            return null;
        }
    }
}
