/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.app;

import com.dts.util.config.AppConfig;
import com.dts.util.concurrent.ExecutorManager;
import com.dts.util.db.DbAccess;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.Handler;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public abstract class AbstractApplication implements IApplication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initConfig(String logFolder, String configFolder, String log4jFileName, String configFileName) {
        File file = new File(logFolder);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(configFolder);
        if (!file.exists()) {
            file.mkdirs();
        }

        AppConfig.getInstance().init(new File(configFolder + File.separator + configFileName));
        logger.info("Configuration Initiated");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                addShutdownHook();
            }
        }, "HOOK    "));
    }

    protected void addShutdownHook() {
        logger.info("Asked to stop!");
        logger.info("Goodbye.");
    }

    @Override
    public void setupDatabaseAccess() {
        Connection conn = null;
        try {
            conn = DbAccess.getInstance().getConnection();
        } catch (SQLException ex) {
            logger.error("", ex);
        } finally {
            DbUtils.closeQuietly(conn);
        }
//        String enableMonitor = config.getProperty("enableMonitor", "true", "jdbc");
//        if (enableMonitor.equalsIgnoreCase("true")) {
//            logger.debug("DB statistic is enabled, so scheduling it");
//            long delay = Long.parseLong(config.getProperty("monitorDelayMs", "60000", "jdbc"));
//            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("db-monitor-%d").build();
//            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
////            ExecutorManager.getInstance().addExecutor("DB statistic", executor);
//            DbAccess2.getInstance().enableMonitor(executor, delay, delay);
//        }
        logger.info("Done setup Database access");
    }

    public void setupExecutorManager() {
        ExecutorManager.getInstance().createThreadPools();
        ExecutorManager.getInstance().scheduleAllTasks();
        logger.info("Done setup ExecutorManager");
    }

    public void setupWebServices() {
        XMLConfiguration config = AppConfig.getInstance().getConfiguration();
        List<HierarchicalConfiguration<ImmutableNode>> webServices = config.configurationsAt("webServices");
        webServices.forEach((webServiceNode) -> {
            try {
                String webServiceName = webServiceNode.getString("name", "");
                String className = webServiceNode.getString("class", "");
                Endpoint endPoint = Endpoint.create(Class.forName(className).newInstance());
                List<Handler> handlerChain = endPoint.getBinding().getHandlerChain();
                handlerChain.add(new WsServerLoggingHandler());
                endPoint.getBinding().setHandlerChain(handlerChain);

                String url = webServiceNode.getString("url", "");
                endPoint.publish(url);
                logger.info("Web service [{}] was published successfuly. WSDL URL: {}?WSDL", webServiceName, url);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        });
    }

}
