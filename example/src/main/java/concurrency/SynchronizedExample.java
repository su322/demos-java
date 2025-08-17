package concurrency;

public class SynchronizedExample {
    private int counter = 0;

    // 使用同步代码块实现线程安全的自增方法
    public void increment() {
        synchronized (this) { // 锁对象应与需要保护的共享资源有直接关联。比如保护某个实例变量，通常用 this；保护静态变量，则用类对象（Class）。
            counter++;
            System.out.println(Thread.currentThread().getName());
        }
    }

    // 使用同步方法实现线程安全的自增方法
    // public synchronized void increment() {
    //     counter++;
    // }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedExample example = new SynchronizedExample();
        int threadCount = 10;
        int incrementsPerThread = 1000;
        Thread[] threads = new Thread[threadCount];

        // 创建并启动多个线程
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.increment(); // 这个是同步的，虽然线程执行顺序不保证，但同步机制保证了最终结果的正确性和一致性。
                }
            });
            threads[i].start();
        }

        // 等待所有线程结束
        for (Thread t : threads) {
            t.join();
        }

        // 输出最终计数结果
        System.out.println("最终计数器值: " + example.getCounter());
        System.out.println("理论值: " + (threadCount * incrementsPerThread));
    }
}
