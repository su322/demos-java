package concurrency.casproblems;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * CAS三大问题：ABA问题，循环性能开销，只能保证一个变量的原子操作
 */

public class ABAProblem {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger atomicInt = new AtomicInteger(100);

        Thread t1 = new Thread(() -> {
            int expected = atomicInt.get(); // 读取到A=100
            try {
                Thread.sleep(1000); // 等待t2操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean success = atomicInt.compareAndSet(expected, 101); // CAS尝试修改为B=101
            System.out.println("线程1 CAS结果: " + success + ", 当前值: " + atomicInt.get());
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(500); // 保证t1先读取
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicInt.compareAndSet(100, 200); // 改成C=200
            atomicInt.compareAndSet(200, 100); // 又改回A=100
            System.out.println("线程2 完成ABA操作, 当前值: " + atomicInt.get());
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
