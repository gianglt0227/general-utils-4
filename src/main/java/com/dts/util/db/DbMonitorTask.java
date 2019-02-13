/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class DbMonitorTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        DataSource dataSource = DbAccess.getInstance().getDataSource();
        if (dataSource != null) {
            try {
                HikariDataSource ds = (HikariDataSource) dataSource;
                Map<String, Object> statisticMap = new TreeMap<>();
                statisticMap.put("maxPoolSize", ds.getMaximumPoolSize());
                statisticMap.put("minimumIdle", ds.getMinimumIdle());
//                statisticMap.put("numBusyConnections", ds.getNumBusyConnectionsDefaultUser());
//                statisticMap.put("numConnections", ds.getNumConnectionsDefaultUser());
//                statisticMap.put("numIdleConnections", ds.getNumIdleConnectionsDefaultUser());

                Set<String> keySet = statisticMap.keySet();
                StringBuilder sb = new StringBuilder();
                for (String key : keySet) {
                    sb.append(key).append(": ").append(statisticMap.get(key)).append(" | ");
                }
                logger.debug("Db Statistic: {}", sb.toString());
            } catch (Exception ex) {
                logger.error("Error checking DB connection statistic: ", ex);
            }
        }
    }

}
