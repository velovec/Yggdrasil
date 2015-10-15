package ru.linachan.valhalla;

import ru.linachan.yggdrasil.component.YggdrasilComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ValhallaCore extends YggdrasilComponent {

    private Map<String, ValhallaTask> taskMap = new HashMap<>();

    private ScheduledExecutorService executorService;

    @Override
    protected void onInit() {
        Integer poolSize = Integer.valueOf(core.getConfig("ValhallaPoolSize", "10"));

        this.executorService = Executors.newScheduledThreadPool(poolSize);
    }

    @Override
    protected void onShutdown() {
        for (String taskName: taskMap.keySet()) {
            taskMap.get(taskName).cancelTask();
        }
    }

    @Override
    public boolean executeTests() {
        ValhallaTask simpleTask = new ValhallaTask("simpleTask", new ValhallaRunnable(core) {
            @Override
            public void run() {
                logInfo("SimpleTask: Completed!");
            }

            @Override
            public void onCancel() {}
        });

        ValhallaTask simplePeriodicTask = new ValhallaTask("simplePeriodicTask", new ValhallaRunnable(core) {
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
                core.logInfo("ValhallaCore: Periodic task '" + task.getTaskName() + "' scheduled with delay: " + task.getInitialDelay());
            } else {
                taskHandle = executorService.schedule(
                        task.getRunnableTask(),
                        task.getInitialDelay(),
                        task.getTimeUnit()
                );
                task.setTaskHandle(taskHandle);
                taskMap.put(task.getTaskName(), task);
                core.logInfo("ValhallaCore: Task '" + task.getTaskName() + "' scheduled with delay: " + task.getInitialDelay());
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
}
