package concurrency.casproblems;

import java.util.concurrent.atomic.AtomicInteger;

public class CASLoopCostProblemFix {
    private final AtomicInteger state = new AtomicInteger(0); // 0: unlocked, 1: locked
    private final Object syncLock = new Object();
    private static final int MAX_SPIN = 500000; // 说实话我不知道这个值设成多少，因为我没有看CPU占用。这个需要和sleep时间一起调整才能同时看到获得锁后的三种输出，不同电脑不一样。

    public void lock() throws InterruptedException {
        int spinCount = 0;
        while (true) {
            if (state.compareAndSet(0, 1)) {
                // 获得锁
                if (spinCount > 0 && spinCount <= MAX_SPIN) {
                    System.out.println(Thread.currentThread().getName() + " 获得锁, 自旋次数: " + spinCount);
                } else if (spinCount > MAX_SPIN) {
                    System.out.println(Thread.currentThread().getName() + " 获得锁, 自旋次数（包含MAX_SPIN）: " + spinCount);
                } else {
                    System.out.println(Thread.currentThread().getName() + " 直接获得锁, 自旋次数: " + spinCount);
                }
                return;
            }
            spinCount++;
            // System.out.println 是阻塞操作，影响自旋速度，因为每个线程自旋变慢，锁持有者释放锁后，其他线程“慢慢”地尝试 CAS，竞争变得不激烈，导致自旋次数变少。
//            System.out.println(Thread.currentThread().getName() + " " + spinCount);
            if (spinCount > MAX_SPIN) {
                System.out.println(Thread.currentThread().getName() + " 自旋超过阈值(" + MAX_SPIN + "), 挂起等待");
                synchronized (syncLock) {
                    while (state.get() != 0) {
                        syncLock.wait(); // 挂起线程，直到锁可用
                    }
                    System.out.println(Thread.currentThread().getName() + " 被唤醒, 继续尝试获取锁");
                }
//                spinCount = 0; // 重置自旋计数
            }
        }
    }

    public void unlock() {
        state.set(0); // 解锁
        synchronized (syncLock) {
            syncLock.notifyAll(); // 唤醒等待的线程
        }
    }

    public static void main(String[] args) {
        CASLoopCostProblemFix casLock = new CASLoopCostProblemFix();
        int threadCount = 5;
        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 尝试获取锁");
                    casLock.lock();
                    Thread.sleep(5); // 持有锁一段时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    casLock.unlock();
                    System.out.println(Thread.currentThread().getName() + " 释放锁");
                }
            }).start();
        }
    }
}
