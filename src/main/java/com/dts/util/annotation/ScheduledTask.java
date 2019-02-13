package com.dts.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledTask {
    String taskName();
    String executorName();
    int corePoolSize() default 1;
    boolean runOnStart() default false;

    ScheduleType scheduleType() default ScheduleType.FIXED_RATE;
    int intialDelay() default 0;
    int period() default 10;
    int delay() default 10;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
    String startTime();
}
