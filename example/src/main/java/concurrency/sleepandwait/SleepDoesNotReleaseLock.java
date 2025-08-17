package concurrency.sleepandwait;

class SleepDoesNotReleaseLock {
    // 如果 lock 需要被 main 方法以外的其他方法、类或线程访问（比如跨实例、跨方法），就必须用 static 或成员变量。
    // 如果只在 main 方法里用，局部变量就足够了，这个就是可以写在 main 里面。
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread sleepingThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread 1 会继续持有锁，并且进⼊睡眠状态");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread 1 醒来了，并且释放了锁");
            }
        });

        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread 2 进⼊同步代码块");
            }
        });
        sleepingThread.start();
        Thread.sleep(1000);
        waitingThread.start();
    }
}
