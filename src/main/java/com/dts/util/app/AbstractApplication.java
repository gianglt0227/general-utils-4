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
import lombok.extern.slf4j.Slf4j;
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

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initConfig(File configFile) {
        AppConfig.getInstance().init(configFile);
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

    public void setupServlets(
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
                handler.addServletWithMapping(servletClass.getName(), annotation.urlPatterns()[0]);
                logger.debug("Started WebServlet {} at context: {}", annotation.name(), annotation.urlPatterns()[0]);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }

        // Start the server
        server.start();
        server.join();
    }

}
