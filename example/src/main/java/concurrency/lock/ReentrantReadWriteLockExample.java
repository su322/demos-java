package concurrency.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.ArrayList;
import java.util.List;

/**
 * ReentrantReadWriteLock 示例：演示如何用读写锁保护共享资源，实现线程安全的读写操作。
 */

public class ReentrantReadWriteLockExample {
    // 共享资源
    private final List<String> data = new ArrayList<>();
    // 创建读写锁
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    // 读锁
    private final Lock readLock = rwLock.readLock();
    // 写锁
    private final Lock writeLock = rwLock.writeLock();

    /**
     * 读操作，使用读锁保护，允许多个线程同时读取。
     */
    public List<String> readData() {
        readLock.lock();
        try {
            // 读操作可以并发执行
            return new ArrayList<>(data);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 写操作，使用写锁保护，写时只允许一个线程访问。
     */
    public void writeData(String value) {
        writeLock.lock();
        try {
            // 写操作需要独占锁
            data.add(value);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 测试方法，演示多个线程读写。
     */
    public static void main(String[] args) {
        ReentrantReadWriteLockExample example = new ReentrantReadWriteLockExample();

        // 写线程
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                example.writeData("data-" + i);
                System.out.println("写入: data-" + i);
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });

        // 读线程
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                List<String> current = example.readData();
                System.out.println("读取: " + current);
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });

        writer.start();
        reader.start();
    }
}
