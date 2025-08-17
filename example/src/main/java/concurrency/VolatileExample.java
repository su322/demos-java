package concurrency;

/**
 * VolatileExample 主要体现了 volatile 关键字的两个核心作用：
 * 1. 可见性
 * 使用 volatile 修饰的变量（如 running），可以保证当一个线程修改该变量的值后，其他线程能够立即看到最新的值。
 * 在你的代码中，主线程将 running 设为 false，工作线程能够及时感知到这个变化，跳出 while 循环，安全退出。
 * 2. 适用于状态标志的线程间通信
 * volatile 适合用于线程间的简单状态通知（如停止标志），无需加锁，性能开销低。
 * 你的例子中，主线程通过修改 running 变量，通知工作线程停止工作，这是一种典型的线程间通信场景。
 * 补充说明：
 * volatile 不能保证复合操作的原子性（如 counter++），只保证单个变量的可见性和有序性。
 * 如果没有 volatile，工作线程可能会一直在自己的缓存中读取 running 的旧值，导致无法及时退出循环。
 * 结论：
 * VolatileExample 体现了 volatile 的可见性特性和在多线程状态通知中的典型用法，适合用于简单的线程间通信和状态标志。
 * <p>
 *
 * volatile 关键字能保证可见性，而普通变量没有这个保证。
 * 原因如下：
 * 1. Java 内存模型
 * 普通变量在多线程环境下，每个线程可能会把变量的值缓存在自己的工作内存（CPU缓存）里，线程之间不会及时刷新主内存的数据。这样，一个线程修改了变量，其他线程可能看不到最新的值。
 * 2. volatile 的作用
 * 用 volatile 修饰的变量，每次读写都直接操作主内存，不会用线程本地缓存。只要一个线程修改了 volatile 变量，其他线程立刻能看到最新的值，保证了可见性。
 * 3. 举例说明
 * 普通变量：主线程把 running 设为 false，工作线程可能一直在自己的缓存里读到 true，导致无法退出循环。
 * volatile变量：主线程把 running 设为 false，工作线程会立刻感知到变化，跳出循环。
 * <p>
 *
 * 主内存和线程本地缓存是 Java 内存模型（JMM）中的两个重要概念，它们的关系如下：
 * 1. 主内存（Main Memory）
 * 主内存是所有线程共享的内存区域，存放着所有变量的真实值。每个线程对变量的最终修改都必须写回主内存，其他线程读取变量时也要从主内存获取最新值。
 * 2. 线程本地缓存（Working Memory/Local Cache）
 * 每个线程都有自己的本地缓存（工作内存），用于临时存储主内存中的变量副本。线程在执行时，通常会先把主内存中的变量读到本地缓存，然后在本地缓存中进行操作，最后再把结果写回主内存。
 * 3. 关系与问题
 * 如果线程只在本地缓存中操作变量，而不及时同步到主内存，其他线程就无法看到最新的变量值，导致“可见性问题”。
 * 这就是为什么普通变量在多线程环境下可能出现数据不一致：一个线程修改了变量，其他线程可能还在用旧值。
 * volatile 关键字可以保证变量的修改会立即同步到主内存，其他线程也会从主内存读取最新值，从而解决可见性问题。
 * 4. 举例说明
 * 主线程把 running 设为 false，如果 running 是普通变量，工作线程可能一直在自己的本地缓存里读到 true，无法及时退出循环。
 * 如果 running 是 volatile 变量，主线程修改后会立即同步到主内存，工作线程每次都从主内存读取最新值，能及时感知变化。
 */

public class VolatileExample {
    // 用 volatile 修饰的变量，每次读写都直接操作主内存，不会用线程本地缓存。只要一个线程修改了 volatile 变量，其他线程立刻能看到最新的值，保证了可见性。
    private volatile boolean running = true;
    private int counter = 0;

    public void start() throws InterruptedException {
        Thread worker = new Thread(() -> {
            while (running) {
                counter++; // 注意，如果 running 不变是不会停的
            }
            System.out.println("工作线程结束，计数器: " + counter);
        });
        worker.start();

        // 主线程休眠1秒后修改running
        Thread.sleep(1000);
        running = false;
        worker.join();
        System.out.println("主线程结束");
    }

    public static void main(String[] args) throws InterruptedException {
        new VolatileExample().start();
    }
}
