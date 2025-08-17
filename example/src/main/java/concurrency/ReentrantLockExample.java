package concurrency;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 具有以下主要特点：
 * 1. 可重入性
 * 同一个线程可以多次获得同一个锁（递归加锁），不会死锁。即如果一个线程已经持有锁，再次请求锁时会直接成功，内部有计数器记录加锁次数，解锁时需与加锁次数匹配。
 * 2. 显示锁/手动控制
 * 需要手动调用 lock.lock() 获取锁，lock.unlock() 释放锁。相比 synchronized，锁的获取和释放更加灵活，可以在不同方法间控制锁的范围。
 * 3. 可中断锁获取
 * 支持 lock.lockInterruptibly()，允许线程在等待锁时响应中断，避免死等。
 * 4. 尝试获取锁
 * 支持 lock.tryLock()，可以立即返回是否获取到锁，或指定超时时间，适合需要避免长时间阻塞的场景。
 * 5. 公平锁/非公平锁
 * 构造时可指定公平性（new ReentrantLock(true)），公平锁保证线程按请求顺序获得锁，非公平锁则可能插队，性能更高。
 * 6. 支持条件变量
 * 可通过 lock.newCondition() 创建 Condition 对象，实现更灵活的线程等待/唤醒机制（类似 Object 的 wait/notify，但更强大）。
 * 7. 可用于多种同步场景
 * 适合复杂同步需求，如分段锁、读写锁、死锁检测等，功能比 synchronized 更丰富。
 * 总结：
 * ReentrantLock 提供了比 synchronized 更灵活和强大的锁机制，适合需要高级并发控制的场景。
 */

public class ReentrantLockExample {
    private int counter = 0;
    private final ReentrantLock lock = new ReentrantLock();

    // 线程安全的自增方法
    public void increment() {
        lock.lock(); // 这里
        try {
            counter++;
        } finally {
            lock.unlock(); // 这里
        }
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockExample example = new ReentrantLockExample();
        int threadCount = 10;
        int incrementsPerThread = 10000;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.increment();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("最终计数器值: " + example.getCounter());
        System.out.println("理论值: " + (threadCount * incrementsPerThread));
    }
}
