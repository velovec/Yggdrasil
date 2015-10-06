package ru.linachan.valhalla;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ValhallaTask {

    private String taskName;
    private ValhallaRunnable runnableTask;
    private Integer initialDelay = 0;

    private Boolean isPeriodic = false;
    private Integer executionPeriod;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private ScheduledFuture<?> taskHandle;

    public ValhallaTask(String taskName, ValhallaRunnable runnableTask) {
        this.taskName = taskName;
        this.runnableTask = runnableTask;
    }

    public ValhallaTask(String taskName, ValhallaRunnable runnableTask, Integer initialDelay) {
        this.taskName = taskName;
        this.runnableTask = runnableTask;
        this.initialDelay = initialDelay;
    }

    public ValhallaTask(String taskName, ValhallaRunnable runnableTask, Integer initialDelay, Integer executionPeriod) {
        this.taskName = taskName;
        this.runnableTask = runnableTask;
        this.initialDelay = initialDelay;
        this.isPeriodic = true;
        this.executionPeriod = executionPeriod;
    }

    public ValhallaTask(String taskName, ValhallaRunnable runnableTask, Integer initialDelay, Integer executionPeriod, TimeUnit timeUnit) {
        this.taskName = taskName;
        this.runnableTask = runnableTask;
        this.initialDelay = initialDelay;
        this.isPeriodic = true;
        this.executionPeriod = executionPeriod;
        this.timeUnit = timeUnit;
    }

    public void setExecutionPeriod(Integer executionPeriod, TimeUnit timeUnit) {
        this.executionPeriod = executionPeriod;
        this.isPeriodic = true;
        this.timeUnit = timeUnit;
    }

    public String getTaskName() {
        return taskName;
    }

    public Runnable getRunnableTask() {
        return runnableTask;
    }

    public Integer getInitialDelay() {
        return initialDelay;
    }

    public Boolean isPeriodic() {
        return isPeriodic;
    }

    public Integer getExecutionPeriod() {
        return executionPeriod;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTaskHandle(ScheduledFuture<?> taskHandle) {
        this.taskHandle = taskHandle;
    }

    public void cancelTask() {
        this.runnableTask.onCancel();
        this.taskHandle.cancel(true);
    }

    public boolean isDone() {
        return this.taskHandle.isDone();
    }
}
