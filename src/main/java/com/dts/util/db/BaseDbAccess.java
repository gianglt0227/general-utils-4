/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

import com.dts.util.config.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.configuration2.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public abstract class BaseDbAccess {

    protected DataSource dataSource;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    protected DataSource setupDataSource() {
        try {
            logger.info("Setting up datasource");
            //            String jdbcUrl = "jdbc:mysql://localhost:3306/cbcdb?zeroDateTimeBehavior=convertToNull";
            //            String userName = "root";
            //            String password = "123456";
            //            String classDriverName = "com.mysql.jdbc.Driver";
            XMLConfiguration config = AppConfig.getInstance().getConfiguration();

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getString("jdbc.jdbcUrl", "jdbc:oracle:thin:@192.168.6.248:1521:mca"));
            hikariConfig.setUsername(config.getString("jdbc.user", "oracle.jdbc.OracleDriver"));
            hikariConfig.setPassword(config.getString("jdbc.password", "livescreen"));
            hikariConfig.setAutoCommit(config.getBoolean("jdbc.autoCommit", false));
            hikariConfig.setConnectionTimeout(config.getLong("jdbc.connectionTimeout", 30000));
            hikariConfig.setIdleTimeout(config.getLong("jdbc.idleTimeout", 600000));
            hikariConfig.setMaxLifetime(config.getLong("jdbc.maxLifetime", 600000));
            hikariConfig.setMinimumIdle(config.getInt("jdbc.minimumIdle", 3));
            hikariConfig.setMaximumPoolSize(config.getInt("jdbc.maximumPoolSize", 10));

            String driverClassName = config.getString("jdbc.driverClassName", "livescreen");
            if (driverClassName.contains("mysql") || driverClassName.contains("mariadb")) {
                hikariConfig.addDataSourceProperty("cachePrepStmts", config.getString("jdbc.mysql.cachePrepStmts", "true"));
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", config.getString("jdbc.mysql.prepStmtCacheSize", "250"));
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", config.getString("jdbc.mysql.prepStmtCacheSqlLimit", "2048"));
                hikariConfig.addDataSourceProperty("useServerPrepStmts", config.getString("jdbc.mysql.useServerPrepStmts", "true"));
                hikariConfig.addDataSourceProperty("useLocalSessionState", config.getString("jdbc.mysql.useLocalSessionState", "true"));
                hikariConfig.addDataSourceProperty("useLocalTransactionState", config.getString("jdbc.mysql.useLocalTransactionState", "true"));
                hikariConfig.addDataSourceProperty("rewriteBatchedStatements", config.getString("jdbc.mysql.rewriteBatchedStatements", "true"));
                hikariConfig.addDataSourceProperty("cacheResultSetMetadata", config.getString("jdbc.mysql.cacheResultSetMetadata", "true"));
                hikariConfig.addDataSourceProperty("cacheServerConfiguration", config.getString("jdbc.mysql.cacheServerConfiguration", "true"));
                hikariConfig.addDataSourceProperty("elideSetAutoCommits", config.getString("jdbc.mysql.elideSetAutoCommits", "true"));
                hikariConfig.addDataSourceProperty("maintainTimeStats", config.getString("jdbc.mysql.maintainTimeStats", "false"));
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
