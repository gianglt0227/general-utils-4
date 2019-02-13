/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.concurrent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class ExecutorsMonitor implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        try {
            logPoolsInfo();
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    private void logPoolsInfo() {
        Set<Map.Entry<String, ExecutorService>> executors = ExecutorManager.getInstance().getExecutors();
        for (Map.Entry<String, ExecutorService> entry : executors) {
            if (entry.getValue() instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) entry.getValue();
                String poolName = entry.getKey();
                int numOfThreadInPool = executor.getPoolSize();
                int activeThreads = executor.getActiveCount();
                long completedTaskCount = executor.getCompletedTaskCount();
                long submittedTaskCount = executor.getTaskCount();
                int queueSize = executor.getQueue().size();
                int remainingQueueSize = executor.getQueue().remainingCapacity();
                logger.debug("Pool: {} | ActiveThreads: {} | ThreadsInPool: {} | QueueSize: {} RemainingQueueSize: {} | SubmittedTasks: {}| CompletedTasks: {}",
                        new Object[]{poolName, activeThreads, numOfThreadInPool, queueSize, remainingQueueSize, submittedTaskCount, completedTaskCount});
            }
        }
    }

}
