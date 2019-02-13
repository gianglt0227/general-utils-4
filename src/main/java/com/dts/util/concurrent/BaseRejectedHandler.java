/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class BaseRejectedHandler implements RejectedExecutionHandler {

    private final Logger logger = LoggerFactory.getLogger(BaseRejectedHandler.class);
    private final String poolName;

    public BaseRejectedHandler(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.error("Thread pool overloaded: {}", poolName);
    }
}
