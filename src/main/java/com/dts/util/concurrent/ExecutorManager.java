/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.concurrent;

//import com.elcom.luckymusic.provisioning.Constant;

import com.dts.util.annotation.ScheduledTask;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.reflections.Reflections;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author GiangLT
 */
@Slf4j
public class ExecutorManager {

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

    public static void main(String[] args) {
        ExecutorManager em = new ExecutorManager();
        int initialDelaySec = em.getInitialDelaySec("21:00:00", 5, TimeUnit.NANOSECONDS);
        System.out.println(initialDelaySec);

    }

    public synchronized ExecutorService addExecutor(String executorName, ExecutorService executor) {
        log.trace("Adding executor {}", executorName);
        return executorMap.putIfAbsent(executorName, executor);
    }

    public synchronized ExecutorService updateExecutor(String executorName, ExecutorService executor) {
        log.trace("Updating executor {}", executorName);
        return executorMap.put(executorName, executor);
    }

    public synchronized ExecutorService removeExecutor(String executorName) {
        log.trace("Removing executor {}", executorName);
        return executorMap.remove(executorName);
    }

    public ExecutorService getExecutor(String executorName) {
//        log.trace("Returning executor {}", executorName);
        ExecutorService executorService = executorMap.get(executorName);
        if (executorService == null) {
            executorService = createDefaultThreadPool(executorName);
            this.addExecutor(executorName, executorService);
        }
        return executorService;
    }

    public ScheduledThreadPoolExecutor getScheduledExecutor(String executorName) {
//        log.trace("Returning executor {}", executorName);
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
        log.trace("Creating pool: {}", poolName);

        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueSize, true);
        RejectedExecutionHandler rejectedExecutionHandler = new BaseRejectedHandler(poolName);
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(baseThreadName + "-%d").build();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue, threadFactory, rejectedExecutionHandler);
        executor.prestartAllCoreThreads();
        log.debug("Done creating pool: {}", poolName);

        return executor;
    }

    public void scheduleAllTasks(@NonNull String scannedPackage) {
        Reflections reflections = new Reflections(scannedPackage);
        Set<Class<?>> scheduledTaskClasses = reflections.getTypesAnnotatedWith(ScheduledTask.class);

        for (Class<?> scheduledTaskClass : scheduledTaskClasses) {
            try {
                ScheduledTask annotation = scheduledTaskClass.getAnnotation(ScheduledTask.class);
                String taskName = annotation.taskName();
                Object object = scheduledTaskClass.newInstance();
                if (object instanceof Runnable) {
                    Runnable runnable = (Runnable) object;
                    String executorName = annotation.executorName();
                    int corePoolSize = annotation.corePoolSize();
                    long period = annotation.period();
                    String startTime = annotation.startTime();
                    TimeUnit timeUnit = annotation.timeUnit();

                    RejectedExecutionHandler rejectedExecutionHandler = new BaseRejectedHandler(executorName);
                    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(taskName + "-%d").build();
                    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler);
                    executor.prestartAllCoreThreads();
                    this.addExecutor(executorName, executor);

                    long initialDelay = 0;
                    if (startTime != null && !startTime.isEmpty()) {
                        log.debug("{} will start at {}, and repeat every {} {}", taskName, startTime, period, timeUnit);
                        initialDelay = getInitialDelaySec(startTime, period, timeUnit);
                        log.debug("{} initialDelay is {} seconds", taskName, initialDelay);
                    } else {
                        log.debug("{} will repeat every {} {}", taskName, period, timeUnit);
                    }
                    log.debug("{} first run will be in {} seconds", taskName, initialDelay);

                    if (TimeUnit.MILLISECONDS.equals(timeUnit)
                            || TimeUnit.MICROSECONDS.equals(timeUnit)
                            || TimeUnit.NANOSECONDS.equals(timeUnit)) {
                        initialDelay = timeUnit.convert(initialDelay, TimeUnit.SECONDS);
                    } else {
                        period = timeUnit.toSeconds(period);
                        timeUnit = TimeUnit.SECONDS;
                    }

                    for (int i = 0; i < corePoolSize; i++) {
                        log.trace("!!Scheduling: {}, initDelay: {}, period: {}, TimeUnit: {}", taskName, initialDelay, period, timeUnit);
                        executor.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
                    }

                    boolean runOnStart = annotation.runOnStart();
                    if (runOnStart) {
                        executor.execute(runnable);
                    }
                }
            } catch (Exception ex) {
                log.error("", ex);
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
}
