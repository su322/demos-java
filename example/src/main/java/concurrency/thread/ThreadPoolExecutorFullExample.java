package concurrency.thread;

import java.util.concurrent.*;

/**
 * 复杂线程池示例，体现ThreadPoolExecutor所有参数的用法。
 * 包含：核心线程数、最大线程数、存活时间、阻塞队列、线程工厂、拒绝策略等。
 * <p>
 * ThreadPoolExecutor参数说明：
 * 1. corePoolSize（核心线程数）：线程池中始终保留的线程数量，即使空闲也不会回收。
 * 2. maximumPoolSize（最大线程数）：线程池允许的最大线程数量。
 * 3. keepAliveTime（空闲线程存活时间）：非核心线程空闲多久后会被回收。
 * 4. unit（时间单位）：keepAliveTime的时间单位（如秒、毫秒等）。
 * 5. workQueue（阻塞队列）：用于保存等待执行的任务（如ArrayBlockingQueue、LinkedBlockingQueue等）。
 * 6. threadFactory（线程工厂）：用于创建新线程，可自定义线程名、优先级等。
 * 7. handler（拒绝策略）：当队列满且线程数达到最大时，如何处理新任务（如AbortPolicy、CallerRunsPolicy、DiscardPolicy、DiscardOldestPolicy，也可自定义）。
 * <p>
 * 参数设置建议：
 * - corePoolSize和maximumPoolSize根据业务并发量和机器性能合理设置。
 * - keepAliveTime适合IO密集型任务设置较长，CPU密集型可设置较短。
 * - workQueue容量影响任务排队和拒绝策略触发时机，还有非核心线程创建时机。
 * - threadFactory可用于日志、监控、分组等。
 * - handler根据业务容忍度选择合适策略。
 */

public class ThreadPoolExecutorFullExample {
    public static void main(String[] args) throws InterruptedException {
//        System.out.println(Runtime.getRuntime().availableProcessors());
        ThreadPoolExecutor executor = getThreadPoolExecutor();

        // 4. 提交任务，如果超过最大线程数且任务队列已满，触发拒绝策略
        int missionSize = 1100;
        for (int i = 0; i < missionSize; i++) {
            final int id = i;
            executor.submit(() -> {
                System.out.println("任务" + id + " 开始执行，线程：" + Thread.currentThread().getName());
                // CPU密集型任务：增加计算量，便于观察CPU占用和线程池调度
                long result = 0;
                for (int round = 0; round < 5; round++) { // 外层循环，增加计算量
                    for (int k = 2; k < 1_000_000; k++) { // 质数判断上限提升到100万
                        boolean isPrime = true;
                        for (int j = 2; j * j <= k; j++) {
                            if (k % j == 0) {
                                isPrime = false;
                                break;
                            }
                        }
                        if (isPrime) {
                            result += k;
                        }
                    }
                }
                System.out.println("任务" + id + " 执行完毕，线程：" + Thread.currentThread().getName() + ", 计算结果: " + result);
            });
            // 输出线程池状态
            System.out.println("提交任务" + id + " 后，池大小: " + executor.getPoolSize()
                    + ", 活跃线程: " + executor.getActiveCount()
                    + ", 队列长度: " + executor.getQueue().size());
        }
        // 当你调用 executor.shutdown() 后，线程池会拒绝新任务（我这里是提交完所有任务后才shutdown的），但已提交的任务会继续执行。此时可以用 awaitTermination 等待所有任务执行完毕。
        // 如果在指定时间内所有任务都执行完毕，返回 true；如果超时仍有任务未完成，返回 false
        executor.shutdown();
        boolean b = executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("所有任务是否已完成？" + b + "，主线程继续执行");
    }

    private static ThreadPoolExecutor getThreadPoolExecutor() {
        // 1. 自定义线程工厂，输出线程创建信息
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setName("自定义线程-" + t.getId());
            System.out.println("创建线程: " + t.getName());
            return t;
        };

        // 2. 自定义拒绝策略，输出被拒绝任务信息
        RejectedExecutionHandler handler = (r, executor) -> {
            System.out.println("任务被拒绝: " + r.toString());
        };

        // 3. 创建线程池，设置所有参数
        // 核心线程数 在一次实验中设置为5就基本打满我的cpu了 其实不能这样做
        // 按照我的实验，当任务总数没有超过队列容量的话就不会创建非核心线程，此时如果设置为5就只用了一半，
        // 如果任务总数超过队列容量，就会创建线程总数到最大线程数也就是刚好是我的处理器核心数量10，
        // 是因为我之前设置的任务数量很多，才产生了核心线程数设置为5就打满cpu的现象
        // 补充：
        // 进程（Process）：操作系统资源分配的最小单位，是一个正在运行的程序。每个进程有独立的内存空间、系统资源（如文件句柄、网络端口等）。
        // 线程（Thread）：进程中的执行单元，是 CPU 调度的最小单位。如果你的 CPU 有 10 个物理核心，那么最多可以有 10 个线程在同一时刻真正并行执行（每个核心分配一个线程）。一个进程可以包含多个线程，这些线程共享进程的内存和资源，但每个线程有自己的执行栈和程序计数器。
        int corePoolSize = 10; // cpu密集型的核心线程数设置为处理器核心数量最好 IO密集型：核心线程数 = CPU核心数 * 2 或更多
        int maximumPoolSize = corePoolSize * 2; // 最大线程数
        long keepAliveTime = 3; // 非核心线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000); // 队列容量
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,      // 核心线程数
                maximumPoolSize,   // 最大线程数
                keepAliveTime,     // 非核心线程存活时间
                unit,              // 时间单位
                queue,             // 阻塞队列
                threadFactory,     // 线程工厂
                handler            // 拒绝策略
        );
        return executor;
    }
}
