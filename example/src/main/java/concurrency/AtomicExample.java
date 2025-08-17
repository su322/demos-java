package concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger 的实现基于 Java 并发包（java.util.concurrent.atomic），其核心原理是利用底层的 CAS（Compare-And-Swap，比较并交换）无锁机制来保证原子性操作。
 * 主要实现原理如下：
 * 1. CAS（Compare-And-Swap）机制
 * CAS 是一种原子操作指令，底层由 CPU 支持。它会比较某个变量的当前值与期望值，如果相等则更新为新值，否则不做任何操作。
 * 例如，incrementAndGet() 方法会不断尝试将当前值加一，直到 CAS 操作成功为止。
 * 2. volatile 保证可见性
 * AtomicInteger 内部的 value 字段是 volatile 修饰，保证多线程下的可见性。
 * 3. 无锁并发
 * 所有原子操作（如自增、自减、加法、CAS等）都不加锁，而是通过 CAS 保证原子性，避免了 synchronized 带来的性能损耗和线程阻塞。
 * 4. Unsafe 类的底层支持
 * AtomicInteger 内部通过 sun.misc.Unsafe 类直接调用 JVM 的原子指令（如 compareAndSwapInt），实现高效的原子操作。
 */

public class AtomicExample {
    private AtomicInteger atomicCounter = new AtomicInteger(0);
    private int unsafeCounter = 0;

    // 线程安全的自增方法
    public void atomicIncrement() {
        atomicCounter.incrementAndGet();
    }

    // 非线程安全的自增方法
    public void unsafeIncrement() {
        unsafeCounter++;
    }

    public int getAtomicCounter() {
        return atomicCounter.get();
    }

    public int getUnsafeCounter() {
        return unsafeCounter;
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicExample example = new AtomicExample();
        int threadCount = 10;
        int incrementsPerThread = 10000;
        Thread[] threads = new Thread[threadCount];

        // 测试 AtomicInteger
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.atomicIncrement();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("AtomicInteger 计数器: " + example.getAtomicCounter());
        System.out.println("理论值: " + (threadCount * incrementsPerThread));

        // 测试非线程安全计数器
        example.unsafeCounter = 0; // 重置
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.unsafeIncrement();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("非线程安全计数器: " + example.getUnsafeCounter());
        System.out.println("理论值: " + (threadCount * incrementsPerThread));
    }
}
