/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.concurrent;

//import com.elcom.luckymusic.provisioning.Constant;
import com.dts.util.config.AppConfig;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class ExecutorManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ExecutorManager instance;
    private final ConcurrentMap<String, ExecutorService> executorMap = new ConcurrentHashMap<>();

    private ExecutorManager() {
    }

    public static synchronized ExecutorManager getInstance() {
        if (instance == null) {
            instance = new ExecutorManager();
        }

        return instance;
    }

    public synchronized ExecutorService addExecutor(String executorName, ExecutorService executor) {
        logger.trace("Adding executor {}", executorName);
        return executorMap.putIfAbsent(executorName, executor);
    }

    public synchronized ExecutorService updateExecutor(String executorName, ExecutorService executor) {
        logger.trace("Updating executor {}", executorName);
        return executorMap.put(executorName, executor);
    }

    public synchronized ExecutorService removeExecutor(String executorName) {
        logger.trace("Removing executor {}", executorName);
        return executorMap.remove(executorName);
    }

    public ExecutorService getExecutor(String executorName) {
//        logger.trace("Returning executor {}", executorName);
        ExecutorService executorService = executorMap.get(executorName);
        if (executorService == null) {
            executorService = createDefaultThreadPool(executorName);
            this.addExecutor(executorName, executorService);
        }
        return executorService;
    }

    public ScheduledThreadPoolExecutor getScheduledExecutor(String executorName) {
//        logger.trace("Returning executor {}", executorName);
        ExecutorService executorService = executorMap.get(executorName);
        if (executorService == null || !(executorService instanceof ScheduledThreadPoolExecutor)) {
            executorService = createDefaultScheduledThreadPool(executorName);
            this.addExecutor(executorName, executorService);
        }
        return (ScheduledThreadPoolExecutor) executorService;
    }

    private ExecutorService createDefaultScheduledThreadPool(String poolName) {
        RejectedExecutionHandler rejectedExecutionHandler = new BaseRejectedHandler(poolName);
        String baseThreadName = poolName + "Thread";
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(baseThreadName + "-%d").build();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory, rejectedExecutionHandler);
        return executor;
    }

    public Set<Entry<String, ExecutorService>> getExecutors() {
        return executorMap.entrySet();
    }

    private ExecutorService createDefaultThreadPool(String poolName) {
        int corePoolSize = 5;
        int maximumPoolSize = 10;
        long keepAliveTime = 60000L;
        int queueSize = 10000;
        String baseThreadName = poolName + "Thread";
        return createThreadPool(poolName, corePoolSize, maximumPoolSize, keepAliveTime, queueSize, baseThreadName);
    }

    private ExecutorService createThreadPool(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, int queueSize, String baseThreadName) {
        logger.trace("Creating pool: {}", poolName);

        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueSize, true);
        RejectedExecutionHandler rejectedExecutionHandler = new BaseRejectedHandler(poolName);
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(baseThreadName + "-%d").build();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue, threadFactory, rejectedExecutionHandler);
        executor.prestartAllCoreThreads();
        logger.debug("Done creating pool: {}", poolName);

        return executor;
    }

    public void createThreadPools() {
        XMLConfiguration xmlconfig = AppConfig.getInstance().getConfiguration();
        List<HierarchicalConfiguration<ImmutableNode>> threadPools = xmlconfig.configurationsAt("threadPools.pool");
        for (HierarchicalConfiguration<ImmutableNode> threadPool : threadPools) {
            try {
                String poolName = threadPool.getString("poolName", "");
                int corePoolSize = threadPool.getInt("corePoolSize", 15);
                int maximumPoolSize = threadPool.getInt("maximumPoolSize", 50);
                long keepAliveTime = threadPool.getLong("keepAliveTime", 60000);
                int queueSize = threadPool.getInt("queueSize", 10000);
                String baseThreadName = threadPool.getString("baseThreadName", poolName);

                ExecutorService executor = createThreadPool(poolName, corePoolSize, maximumPoolSize, keepAliveTime, queueSize, baseThreadName);
                this.addExecutor(poolName, executor);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

    /*
    public void createScheduledThreadPools() {
        XMLConfiguration xmlconfig = AppConfig.getInstance().getConfiguration();
        List<HierarchicalConfiguration<ImmutableNode>> scheduledThreadPools = xmlconfig.configurationsAt("scheduledThreadPools.pool");
        for (HierarchicalConfiguration<ImmutableNode> threadPool : scheduledThreadPools) {
            try {
                String poolName = threadPool.getString("poolName", "");
                logger.trace("Creating scheduled pool: {}", poolName);
                int corePoolSize = threadPool.getInt("corePoolSize", 1);
                RejectedExecutionHandler rejectedExecutionHandler = new BaseRejectedHandler(poolName);
                String baseThreadName = threadPool.getString("baseThreadName", poolName);
                ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(baseThreadName + "-%d").build();
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler);
                executor.prestartAllCoreThreads();

                logger.debug("Done creating scheduled pool: {}", poolName);
                this.addExecutor(poolName, executor);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }
     */
    public void scheduleAllTasks() {
        XMLConfiguration xmlconfig = AppConfig.getInstance().getConfiguration();
        List<HierarchicalConfiguration<ImmutableNode>> scheduledTasks = xmlconfig.configurationsAt("scheduledTasks.task");
        for (HierarchicalConfiguration<ImmutableNode> scheduledTask : scheduledTasks) {
            try {
                String taskName = scheduledTask.getString("taskName", "");
                String qualifiedClassName = scheduledTask.getString("class", "");
                Object object = Class.forName(qualifiedClassName).newInstance();
                if (object instanceof Runnable) {
                    Runnable runnable = (Runnable) object;
                    String executorName = scheduledTask.getString("executorName", "");
                    int corePoolSize = scheduledTask.getInt(".corePoolSize", 1);
                    long period = scheduledTask.getLong("period", 86400 * 1000);
                    String startTime = scheduledTask.getString("startTime", "");
                    String timeUnitStr = scheduledTask.getString("timeUnit", "MILLISECONDS");

                    RejectedExecutionHandler rejectedExecutionHandler = new BaseRejectedHandler(executorName);
                    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(taskName + "-%d").build();
                    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler);
                    executor.prestartAllCoreThreads();
                    this.addExecutor(executorName, executor);

                    long initialDelay = 0;
                    TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr);

                    if (startTime != null && !startTime.isEmpty()) {
                        logger.debug("{} will start at {}, and repeat every {} {}", taskName, startTime, period, timeUnitStr);
                        initialDelay = getInitialDelaySec(startTime, period, timeUnit);
                        logger.debug("{} initialDelay is {} seconds", taskName, initialDelay);
                    } else {
                        logger.debug("{} will repeat every {} {}", taskName, period, timeUnitStr);
                    }
                    logger.debug("{} first run will be in {} seconds", taskName, initialDelay);

                    if (TimeUnit.MILLISECONDS.equals(timeUnit)
                            || TimeUnit.MICROSECONDS.equals(timeUnit)
                            || TimeUnit.NANOSECONDS.equals(timeUnit)) {
                        initialDelay = timeUnit.convert(initialDelay, TimeUnit.SECONDS);
                    } else {
                        period = timeUnit.toSeconds(period);
                        timeUnit = TimeUnit.SECONDS;
                    }

                    for (int i = 0; i < corePoolSize; i++) {
                        logger.trace("!!Scheduling: {}, initDelay: {}, period: {}, TimeUnit: {}", taskName, initialDelay, period, timeUnit);
                        executor.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
                    }

                    boolean runOnStart = scheduledTask.getBoolean("runOnStart", false);
                    if (runOnStart) {
                        executor.execute(runnable);
                    }
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

    private int getInitialDelaySec(String sendTime, long period, TimeUnit timeUnit) throws NumberFormatException {
        List<String> tokens = Splitter.on(':').trimResults().splitToList(sendTime);
        int hour = Integer.parseInt(tokens.get(0));
        int minute = 0;
        int second = 0;
        if (tokens.size() == 3) {
            minute = Integer.parseInt(tokens.get(1));
            second = Integer.parseInt(tokens.get(2));
        } else if (tokens.size() == 2) {
            minute = Integer.parseInt(tokens.get(1));
            second = 0;
        }

        DateTime now = DateTime.now();
        DateTime runTime = DateTime.now().withTime(hour, minute, second, 0);
        int initialDelay;

        if (!runTime.isBeforeNow()) {
            initialDelay = Seconds.secondsBetween(now, runTime).getSeconds();
        } else if (TimeUnit.MILLISECONDS.equals(timeUnit)
                || TimeUnit.MICROSECONDS.equals(timeUnit)
                || TimeUnit.NANOSECONDS.equals(timeUnit)
                || TimeUnit.SECONDS.equals(timeUnit)) {
            initialDelay = 0;
        } else {
            long toSeconds = timeUnit.toSeconds(period);
            while (runTime.isBeforeNow()) {
                runTime = runTime.plusSeconds((int) toSeconds);
                System.out.println(runTime);
            }
            initialDelay = Seconds.secondsBetween(now, runTime).getSeconds();
        }
        return initialDelay;
    }

    public static void main(String[] args) {
        ExecutorManager em = new ExecutorManager();
        int initialDelaySec = em.getInitialDelaySec("21:00:00", 5, TimeUnit.NANOSECONDS);
        System.out.println(initialDelaySec);

    }
}
