package cn.fudoc.trade.core.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskManager {
    // 添加日志记录异常，方便排查问题
    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskManager.class);

    // 线程池（单线程即可满足周期性任务需求）
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("fu-stock-timer"); // 给线程命名，方便排查问题
        thread.setDaemon(false); // 非守护线程，避免主线程退出后任务直接终止（根据需求调整）
        return thread;
    });

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
     * @param initialDelay 初始延迟
     * @param period 执行周期
     * @param unit 时间单位
     */
    public void startTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        // 若已有任务，先停止
        stopTask();

        // 包装任务，添加全局异常捕获
        Runnable wrappedTask = () -> {
            try {
                task.run(); // 执行原任务
            } catch (Exception e) {
                // 捕获所有非运行时异常
                log.warn("定时任务执行异常，任务将继续调度", e);
            } catch (Error e) {
                // 捕获错误（如OOM、StackOverflow等）
                log.warn("定时任务执行发生严重错误，任务将继续调度", e);
            }
        };

        // 提交包装后的任务
        scheduledFuture = executor.scheduleAtFixedRate(wrappedTask, initialDelay, period, unit);
    }

    // 停止当前任务
    public void stopTask() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            // 取消任务：参数 true 表示若任务正在执行，中断它；false 表示等待当前执行完成
            scheduledFuture.cancel(true);
            scheduledFuture = null; // 清空引用，避免重复判断
        }
    }

    public boolean isRunning() {
        return scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone();
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
                log.warn("定时任务线程池强制关闭");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            log.warn("定时任务线程池关闭被中断", e);
            // 恢复中断状态，让上层感知
            Thread.currentThread().interrupt();
        }
    }
}