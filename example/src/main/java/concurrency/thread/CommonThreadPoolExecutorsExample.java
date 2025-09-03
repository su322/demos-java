package concurrency.thread;

import java.util.concurrent.*;

/**
 * 常用线程池创建与使用示例。
 * 演示：
 * 1. newFixedThreadPool 固定线程数线程池
 * 2. newSingleThreadExecutor 单线程池
 * 3. newCachedThreadPool 缓存线程池
 * 4. newScheduledThreadPool 定时/周期线程池
 */

public class CommonThreadPoolExecutorsExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. 固定线程数线程池
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        System.out.println("newFixedThreadPool 提交任务:");
        for (int i = 0; i < 5; i++) {
            final int id = i;
            fixedPool.submit(() -> {
                System.out.println("fixedPool 任务" + id + " 执行，线程:" + Thread.currentThread().getName());
            });
        }
        fixedPool.shutdown();
        fixedPool.awaitTermination(2, TimeUnit.SECONDS);

        // 2. 单线程池
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        System.out.println("newSingleThreadExecutor 提交任务:");
        for (int i = 0; i < 3; i++) {
            final int id = i;
            singlePool.submit(() -> {
                System.out.println("singlePool 任务" + id + " 执行，线程:" + Thread.currentThread().getName());
            });
        }
        singlePool.shutdown();
        singlePool.awaitTermination(2, TimeUnit.SECONDS);

        // 3. 缓存线程池 感觉和缓存什么关系
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        System.out.println("newCachedThreadPool 提交任务:");
        for (int i = 0; i < 5; i++) {
            final int id = i;
            cachedPool.submit(() -> {
                System.out.println("cachedPool 任务" + id + " 执行，线程:" + Thread.currentThread().getName());
            });
        }
        cachedPool.shutdown();
        cachedPool.awaitTermination(2, TimeUnit.SECONDS);

        // 4. 定时/周期线程池
        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);

        System.out.println("newScheduledThreadPool 提交定时任务:");
        scheduledPool.schedule(() -> {
            System.out.println("scheduledPool 定时任务 执行，线程:" + Thread.currentThread().getName());
        }, 1, TimeUnit.SECONDS);

        System.out.println("newScheduledThreadPool 提交周期任务:");
        ScheduledFuture<?> future = scheduledPool.scheduleAtFixedRate(() -> {
            System.out.println("scheduledPool 周期任务 执行，线程:" + Thread.currentThread().getName());
        }, 0, 1, TimeUnit.SECONDS);
        Thread.sleep(3500); // 让周期任务执行几次

        future.cancel(true);
        scheduledPool.shutdown();
        scheduledPool.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("所有线程池任务已完成");
    }
}
