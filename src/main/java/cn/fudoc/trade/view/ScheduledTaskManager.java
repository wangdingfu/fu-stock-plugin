package cn.fudoc.trade.view;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskManager {
    // 线程池（单线程即可满足周期性任务需求）
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    // 用于跟踪当前任务的未来对象
    private ScheduledFuture<?> scheduledFuture;

    /**
     * 启动任务 3秒执行一次
     */
    public void startTask(Runnable task) {
        startTask(task, 1, 3, TimeUnit.SECONDS);
    }

    /**
     * 启动或重启任务（固定速率执行）
     *
     * @param task 需要执行的任务
     */
    public void startTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        // 若已有任务，先停止
        stopTask();
        // 提交新任务并保存 future 对象
        scheduledFuture = executor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    // 停止当前任务
    public void stopTask() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            // 取消任务：参数 true 表示若任务正在执行，中断它；false 表示等待当前执行完成
            scheduledFuture.cancel(true);
        }
    }


    public boolean isRunning() {
        return scheduledFuture != null && !scheduledFuture.isCancelled();
    }

    /**
     * 关闭线程池（不再使用时调用，释放资源）
     */
    public void shutdownExecutor() {
        stopTask();
        executor.shutdown();
        try {
            // 等待线程池终止，超时则强制关闭
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}