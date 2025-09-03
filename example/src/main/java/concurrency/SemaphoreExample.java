package concurrency;

import java.util.concurrent.Semaphore;

/**
 * Semaphore 示例：
 * 演示多个线程并发访问有限资源，Semaphore 控制最大并发数。
 * 适合限流、连接池等场景。
 */

public class SemaphoreExample {
    public static void main(String[] args) {
        int permits = 2; // 最大允许2个线程同时访问
        Semaphore semaphore = new Semaphore(permits);
        int threadCount = 5;
        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    System.out.println("线程" + id + " 尝试获取许可...");
                    semaphore.acquire(); // 获取许可
                    System.out.println("线程" + id + " 获得许可，开始执行");
                    Thread.sleep(1000); // 模拟访问共享资源
                    System.out.println("线程" + id + " 释放许可");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); // 释放许可
                }
            }).start();
        }
    }
}
