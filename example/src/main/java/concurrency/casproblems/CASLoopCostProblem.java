package concurrency.casproblems;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示 CAS 循环性能开销问题：
 * 当多个线程竞争同一个原子变量时，CAS失败会导致自旋重试，造成CPU高占用。
 */

public class CASLoopCostProblem {
    private static final AtomicInteger atomicInt = new AtomicInteger(0);
    private static final int THREADS = 4;
    private static final int INCREMENTS = 100_0000;

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                int count = 0;
                for (int j = 0; j < INCREMENTS; j++) {
                    while (true) {
                        int oldValue = atomicInt.get();
                        if (atomicInt.compareAndSet(oldValue, oldValue + 1)) {
//                            System.out.println("线程" + Thread.currentThread() + "抢占成功");
                            break;
                        }
                        // 为什么放在else{}里次数会多出那么多次？
                        count++;
                        System.out.println("线程" + Thread.currentThread() + "自旋" + count + "次");
                        // CAS失败会不断重试，造成性能开销
                    }
                }
            });
        }
        long start = System.currentTimeMillis();
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        long end = System.currentTimeMillis();
        System.out.println("最终结果: " + atomicInt.get());
        System.out.println("耗时: " + (end - start) + "ms");
    }
}

