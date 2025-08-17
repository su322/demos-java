package concurrency.sleepandwait;

public class WaitReleaseLock {
    private static final Object lock = new Object();
    public static void main(String[] args) throws InterruptedException {
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    long start = System.currentTimeMillis();
                    System.out.println("Thread 1 持有锁，准备释放锁和最多等待 5 秒");
                    lock.wait(5000);
                    long end = System.currentTimeMillis();
                    System.out.println("Thread 1 醒来了，并且退出同步代码块，实际等待了 " + (end - start) + " 毫秒");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread notifyingThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread 2 获取到锁，尝试唤醒等待中的线程");
                lock.notify();
                System.out.println("Thread 2 执⾏完了 notify");
            }
        });
        waitingThread.start();
        Thread.sleep(1000);
        notifyingThread.start();
    }
}
