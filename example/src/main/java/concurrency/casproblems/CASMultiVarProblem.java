package concurrency.casproblems;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示多个变量同时更新的CAS问题：
 * CAS只能保证单个变量的原子性，无法原子性地同时更新多个变量。
 */

public class CASMultiVarProblem {
    private static final AtomicInteger varA = new AtomicInteger(0);
    private static final AtomicInteger varB = new AtomicInteger(0);
    private static final AtomicInteger varC = new AtomicInteger(0);
    private static final AtomicInteger varD = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            // 期望同时将A、B、C、D都加1
            boolean successA = varA.compareAndSet(0, 1);
            boolean successB = varB.compareAndSet(0, 1);
            boolean successC = varC.compareAndSet(0, 1);
            boolean successD = varD.compareAndSet(0, 1);
            System.out.println("线程1: A更新" + successA + ", B更新" + successB + ", C更新" + successC + ", D更新" + successD);
        });

        Thread t2 = new Thread(() -> {
            // 期望同时将A、B、C、D都加1
            // 调整一下顺序，才能观察到无法同时更新多个变量，不调整的话全被t1弄完了
            boolean successC = varC.compareAndSet(0, 2);
            boolean successD = varD.compareAndSet(0, 2);
            boolean successA = varA.compareAndSet(0, 2);
            boolean successB = varB.compareAndSet(0, 2);
            System.out.println("线程2: A更新" + successA + ", B更新" + successB + ", C更新" + successC + ", D更新" + successD);
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("最终A: " + varA.get() + ", 最终B: " + varB.get() + ", 最终C: " + varC.get() + ", 最终D: " + varD.get());
    }
}
