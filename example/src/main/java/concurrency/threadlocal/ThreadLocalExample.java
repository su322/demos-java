package concurrency.threadlocal;

/**
 * ThreadLocal 示例：每个线程拥有独立的变量副本，互不干扰。
 * <p>
 * 每个线程（Thread）对象内部有一个 ThreadLocalMap。
 * 这个 ThreadLocalMap 只属于当前线程，其他线程无法访问。
 * 你可以在同一个线程里创建多个 ThreadLocal 实例（比如 threadLocalA、threadLocalB），它们的值都会存放在这个线程自己的 ThreadLocalMap 里。
 * ThreadLocalMap 的 key 是 ThreadLocal 实例，value 是该线程为这个 ThreadLocal 设置的值。
 */

public class ThreadLocalExample {
    // 定义一个 ThreadLocal 变量
    // public static ThreadLocal<Integer> threadLocalCounter = new ThreadLocal<>();
    private static final ThreadLocal<Integer> threadLocalCounter = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            for (int i = 0; i < 5; i++) {
                // 获取当前线程的变量副本并自增
                // 每个线程在第一次调用 threadLocalCounter.get() 或 threadLocalCounter.set() 时，会为自己创建一个属于自己的副本（存储在该线程的 ThreadLocalMap 里）。
                int value = threadLocalCounter.get();
                threadLocalCounter.set(value + 1);
                System.out.println(threadName + " 的计数器: " + threadLocalCounter.get());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 线程执行完毕后清理副本，防止内存泄漏
                    threadLocalCounter.remove();
                }
            }
        };

        Thread t1 = new Thread(task, "线程A");
        Thread t2 = new Thread(task, "线程B");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
