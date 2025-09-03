package concurrency.lock;

/**
 * 一种排查死锁的步骤
 * jps -l 列出所有正在运行的Java进程
 * jstack xxxxx 查看信息
 * <p>
 * Java stack information for the threads listed above:
 * ===================================================
 * "Thread-1":
 *         at concurrency.lock.DeadLockExample.lambda$main$0(DeadLockExample.java:13)
 *         - waiting to lock <0x000000070fca1918> (a java.lang.Object)
 *         - locked <0x000000070fca1908> (a java.lang.Object)
 *         at concurrency.lock.DeadLockExample$$Lambda/0x00000070010031f8.run(Unknown Source)
 *         at java.lang.Thread.runWith(java.base@21.0.7/Thread.java:1596)
 *         at java.lang.Thread.run(java.base@21.0.7/Thread.java:1583)
 * "Thread-2":
 *         at concurrency.lock.DeadLockExample.lambda$main$1(DeadLockExample.java:23)
 *         - waiting to lock <0x000000070fca1908> (a java.lang.Object)
 *         - locked <0x000000070fca1918> (a java.lang.Object)
 *         at concurrency.lock.DeadLockExample$$Lambda/0x0000007001003408.run(Unknown Source)
 *         at java.lang.Thread.runWith(java.base@21.0.7/Thread.java:1596)
 *         at java.lang.Thread.run(java.base@21.0.7/Thread.java:1583)
 * <p>
 * Found 1 deadlock.
 */

public class DeadLockExample {
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("线程1获得lockA，尝试获得lockB");
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                synchronized (lockB) {
                    System.out.println("线程1获得lockB");
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("线程2获得lockB，尝试获得lockA");
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                synchronized (lockA) {
                    System.out.println("线程2获得lockA");
                }
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}
