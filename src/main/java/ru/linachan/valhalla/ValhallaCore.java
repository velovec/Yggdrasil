package ru.linachan.valhalla;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ValhallaCore {

    private YggdrasilCore yggdrasilCore;
    private Map<String, ValhallaTask> taskMap = new HashMap<>();

    private ScheduledExecutorService executorService;

    public ValhallaCore(YggdrasilCore yggdrasilCore) {
        this.yggdrasilCore = yggdrasilCore;

        Integer poolSize = Integer.valueOf(yggdrasilCore.getConfig("ValhallaPoolSize", "10"));

        this.executorService = Executors.newScheduledThreadPool(poolSize);
    }

    public boolean scheduleTask(ValhallaTask task) {
        ScheduledFuture<?> taskHandle;
        if (!taskMap.containsKey(task.getTaskName())) {
            if (task.isPeriodic()) {
                taskHandle = executorService.scheduleAtFixedRate(
                    task.getRunnableTask(),
                    task.getInitialDelay(),
                    task.getExecutionPeriod(),
                    task.getTimeUnit()
                );
                task.setTaskHandle(taskHandle);
                taskMap.put(task.getTaskName(), task);
                yggdrasilCore.logInfo("ValhallaCore: Periodic task '" + task.getTaskName() + "' scheduled with delay: " + task.getInitialDelay());
            } else {
                taskHandle = executorService.schedule(
                    task.getRunnableTask(),
                    task.getInitialDelay(),
                    task.getTimeUnit()
                );
                task.setTaskHandle(taskHandle);
                taskMap.put(task.getTaskName(), task);
                yggdrasilCore.logInfo("ValhallaCore: Task '" + task.getTaskName() + "' scheduled with delay: " + task.getInitialDelay());
            }
            return true;
        }
        return false;
    }

    public ValhallaTask getTask(String taskName) {
        if (this.taskMap.containsKey(taskName)) {
            return this.taskMap.get(taskName);
        }
        return null;
    }

    public void shutdownValhalla() {
        for (String taskName: taskMap.keySet()) {
            taskMap.get(taskName).cancelTask();
        }
    }

    public boolean execute_tests() {
        ValhallaTask simpleTask = new ValhallaTask("simpleTask", new ValhallaRunnable(yggdrasilCore) {
            @Override
            public void run() {
                logInfo("SimpleTask: Completed!");
            }

            @Override
            public void onCancel() {}
        });

        ValhallaTask simplePeriodicTask = new ValhallaTask("simplePeriodicTask", new ValhallaRunnable(yggdrasilCore) {
            @Override
            public void run() {
                logInfo("SimplePeriodicTask: Completed!");
            }

            @Override
            public void onCancel() {
                logInfo("SimplePeriodicTask: Canceled!");
            }
        }, 0, 10);

        this.scheduleTask(simpleTask);
        this.scheduleTask(simplePeriodicTask);

        this.getTask("simplePeriodicTask").cancelTask();

        return true;
    }
}
