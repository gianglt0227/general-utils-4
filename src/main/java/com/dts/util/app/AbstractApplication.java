/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.app;

import com.dts.util.concurrent.ExecutorManager;
import com.dts.util.config.AppConfig;
import com.dts.util.db.DbAccess;
import lombok.NonNull;
import org.apache.commons.dbutils.DbUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
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
        logger.info("Done setup Database access");
    }

    public void setupExecutorManager(@NonNull String scannedPackage) {
        ExecutorManager.getInstance().scheduleAllTasks(scannedPackage);
        logger.info("Done setup ExecutorManager");
    }

    public void setupHttpRequestHandlers(
            @NonNull String scannedPackage,
            @NonNull String host,
            @NonNull int port,
            @NonNull long idleTimeout) throws Exception {
        // The Server
        Server server = new Server();

        // HTTP connector
        ServerConnector httpServerConnector = new ServerConnector(server);
        httpServerConnector.setHost(host);
        httpServerConnector.setPort(port);
        httpServerConnector.setIdleTimeout(idleTimeout);
        server.addConnector(httpServerConnector);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        Reflections reflections = new Reflections(scannedPackage);
        Set<Class<?>> servletClasses = reflections.getTypesAnnotatedWith(WebServlet.class);
        for (Class<?> servletClass : servletClasses) {
            try {
                WebServlet annotation = servletClass.getAnnotation(WebServlet.class);
                handler.addServletWithMapping(servletClass.getSimpleName(), annotation.urlPatterns()[0]);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }

        // Start the server
        server.start();
        server.join();
    }

}
