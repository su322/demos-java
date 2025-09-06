package concurrency.thread;

/**
 * 虚拟线程与普通线程的主要区别如下：
 * <p>
 * 普通线程由操作系统内核管理，每个线程都映射到一个原生线程，资源消耗大，创建和切换成本高。
 * 虚拟线程由 JVM 管理，是用户态线程，不直接映射到操作系统线程，创建和调度极为轻量。
 * <p>
 * 普通线程每个都需要分配较大的栈空间（通常为1MB），数量受限，容易耗尽系统资源。
 * 虚拟线程只分配很小的栈空间（如几KB），JVM可动态扩展，能支持成千上万甚至更多线程并发。
 * <p>
 * 普通线程的调度依赖操作系统，线程切换涉及内核态和用户态转换，开销大。
 * 虚拟线程的调度完全由 JVM 实现，切换只在用户态完成，效率高，延迟低。
 * <p>
 * 普通线程阻塞时会占用操作系统资源，影响系统吞吐。
 * 虚拟线程阻塞时，JVM会自动挂起和恢复，不会占用底层线程资源，极大提升并发能力。
 * <p>
 * 普通线程适合需要底层控制、与原生线程交互的场景。
 * 虚拟线程适合高并发、“有大量阻塞等待”的IO密集型场景，能显著简化高并发编程。在CPU密集型场景下，虚拟线程优势不明显，但也不会有负面影响。
 * <p>
 * JVM内部维护少量“载体线程”（操作系统线程），成千上万个虚拟线程会被JVM调度到这些载体线程上运行，遇到阻塞时自动让出物理线程。
 * <p>
 * 运行结果：
 * 线程数量: 10000
 * 普通线程总耗时: 357 ms
 * 虚拟线程总耗时: 19 ms
 * <p>
 * 你的 VirtualThreadExample 只做了极其简单的任务（int x = 1 + 1;），没有任何 I/O、没有阻塞、没有实际业务逻辑。主要消耗在于线程的创建和销毁。
 * 所以在这种“纯线程创建/销毁”场景下，虚拟线程的优势被极大放大。
 * 真实业务（如数据库分页导出）主要耗时在I/O操作（数据库、网络、磁盘），而不是线程的创建和销毁。
 * 只要线程池/虚拟线程数量足够，I/O密集型任务的总耗时主要受限于外部系统（数据库、磁盘等），而不是线程调度。
 * 这时，虚拟线程和线程池的差距就被“业务瓶颈”掩盖了。
 * <p>
 * 最后我发现，一句话，最大的优势是极低的创建/销毁/切换开销
 */

public class VirtualThreadExample {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10000;
        System.out.println("线程数量: " + threadCount);

        // 普通线程性能测试
        long startNormal = System.currentTimeMillis();
        Thread[] normalThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            normalThreads[i] = new Thread(() -> {
                // 简单任务：计数
                int x = 1 + 1;
            });
            normalThreads[i].start();
        }
        for (Thread t : normalThreads) {
            t.join(); // 这段循环会让主线程依次等待所有线程都执行完毕，确保所有任务都完成后再统计耗时和输出结果
        }
        long endNormal = System.currentTimeMillis();
        System.out.println("普通线程总耗时: " + (endNormal - startNormal) + " ms");

        // 虚拟线程性能测试
        long startVirtual = System.currentTimeMillis();
        Thread[] virtualThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            virtualThreads[i] = Thread.startVirtualThread(() -> {
                int x = 1 + 1;
            });
        }
        for (Thread t : virtualThreads) {
            t.join();
        }
        long endVirtual = System.currentTimeMillis();
        System.out.println("虚拟线程总耗时: " + (endVirtual - startVirtual) + " ms");
    }
}
