package concurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier 示例：
 * 演示多个线程并发执行任务，所有线程到达屏障后统一继续。
 * CyclicBarrier 可用于多线程阶段同步，适合多任务协同场景。
 * <p>
 * 为什么叫cyclic呢?
 * 特性在于：当所有线程都到达屏障并被释放后，屏障会自动重置，可以再次被同一组或新的线程重复使用。
 * 这非常适合多阶段任务协作，比如多线程分批处理、分阶段计算等场景。
 * 与 CountDownLatch 不同，CountDownLatch 只能用一次，计数器归零后就不能再用。
 */

public class CyclicBarrierExample {
    public static void main(String[] args) {
        int threadCount = 3;
        // 创建一个CyclicBarrier，所有线程到达后执行barrierAction
        CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
            System.out.println("所有子线程已到达屏障点，统一继续执行");
        });

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            new Thread(() -> {
                System.out.println("子线程" + id + " 开始执行");
                try {
                    Thread.sleep(500 + id * 200); // 模拟任务耗时
                    System.out.println("子线程" + id + " 到达屏障点");
                    barrier.await(); // 等待其他线程到达屏障
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("子线程" + id + " 通过屏障，继续后续任务");
            }).start();
        }
    }
}
