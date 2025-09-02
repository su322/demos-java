package concurrency.casproblems;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 在 ABAProblem 基础上的 AtomicStampedReference 使用演示，下面还提到了刚开始写的时候遇到的一个小问题
 */

public class ABAProblemFix {
    public static void main(String[] args) throws InterruptedException {
        AtomicStampedReference<Integer> atomicStampedRef = new AtomicStampedReference<>(100, 0);

        Thread t1 = new Thread(() -> {
            int[] stampHolder = new int[1];
            Integer value = atomicStampedRef.get(stampHolder); // 返回当前的引用值（比如 100），同时把当前的版本号写入 stampHolder[0]。
            int stamp = stampHolder[0];
            try {
                Thread.sleep(1000); // 等待t2操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean success = atomicStampedRef.compareAndSet(value, 101, stamp, stamp + 1); // CAS同时比较版本号
            System.out.println("线程1 CAS结果: " + success + ", 当前值: " + atomicStampedRef.getReference() + ", 当前版本: " + atomicStampedRef.getStamp());
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(500); // 保证t1先读取
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 第一次CAS前，原子获取value和stamp
            int[] stampHolder1 = new int[1];
            Integer value1 = atomicStampedRef.get(stampHolder1);
            int stamp1 = stampHolder1[0];
            System.out.println("t2 第一次CAS前 stamp: " + stamp1 + ", value: " + value1);
            boolean aba1 = atomicStampedRef.compareAndSet(100, 200, stamp1, stamp1 + 1); // 改成200，版本+1
            System.out.println("ABA1: " + aba1 + ", 当前值: " + atomicStampedRef.getReference() + ", 当前版本: " + atomicStampedRef.getStamp());

            // 第二次CAS前，原子获取value和stamp
            int[] stampHolder2 = new int[1];
            Integer value2 = atomicStampedRef.get(stampHolder2); // 200
            int stamp2 = stampHolder2[0];

            // 遇到的问题：
            // 之前的写法：boolean aba2 = atomicStampedRef.compareAndSet(200, 100, stamp2, stamp2 + 1);
            // 这种写法在使用 AtomicStampedReference<Integer> 时，虽然 200 的值和当前对象的值相等，但引用 不一定 相同。
            // AtomicStampedReference 的 compareAndSet 方法底层是用 == 比较对象引用，而不是用 equals 比较值。

            // 如果你用字面量 200 放在 value2 这里，编译器会自动装箱为 Integer.valueOf(200)，但不保证和内部存储的引用一致。
            // 第一次 CAS 可以直接写 100，是因为在 Java 的自动装箱机制下，小整数池（-128 到 127）范围内的 Integer 对象会被缓存，所有 Integer.valueOf(100) 得到的都是同一个对象引用。
            // AtomicStampedReference<integer> atomicStampedRef = new AtomicStampedReference<>(100, 0); 如果这里是超出缓存的数，第一次 CAS 直接写字面量也会失败，因为不是同一个引用
            // 正确做法：必须用 atomicStampedRef.get(stampHolder) 获取当前对象的引用作为 compareAndSet 的参数（而不是字面量），保证引用一致。
            boolean aba2 = atomicStampedRef.compareAndSet(value2, 100, stamp2, stamp2 + 1); // 用value2保证引用一致
            System.out.println("ABA2: " + aba2 + ", 当前值: " + atomicStampedRef.getReference() + ", 当前版本: " + atomicStampedRef.getStamp());
            System.out.println("线程2 完成ABA操作, 当前值: " + atomicStampedRef.getReference() + ", 当前版本: " + atomicStampedRef.getStamp());
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
