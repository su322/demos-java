package concurrency;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount); // 翻译为倒计时闩(shuan)锁？
        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            new Thread(() -> {
                System.out.println("子线程" + id + " 开始执行");
                try {
                    Thread.sleep(500 + id * 200); // 模拟任务耗时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("子线程" + id + " 执行完毕");
                    latch.countDown(); // 计数器减一
                }
            }).start();
        }
        System.out.println("主线程等待所有子线程完成...");
        latch.await(); // 等待直到计数器为0
        System.out.println("所有子线程已完成，主线程继续执行");
    }
}
