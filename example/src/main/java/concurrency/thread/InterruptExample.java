package concurrency.thread;

public class InterruptExample {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) { // 这一行就是线程主动检查“中断标志”的地方
                try {
                    System.out.println("Running...");
                    Thread.sleep(1000); // 模拟工作
                } catch (InterruptedException e) {
                    // 当 Thread.sleep() 被中断时，会抛出 InterruptedException，并清除线程的中断标志。
                    // 如果在 catch 块里没有 break 且没有重新设置中断标志，线程继续循环，回到 while 条件判断。
                    // 由于中断标志已被清除（变为 false），isInterrupted() 返回 false，循环条件仍然成立，线程会继续执行，不会退出。
                    // 如果重新设置了中断标志，在条件判断后就会退出循环。
                    // 其实重新设置中断标志和写 break 语句可以二选一，效果差不多。
                    Thread.currentThread().interrupt();
                    System.out.println("Thread interrupted, exiting...");
//                    break;
                }
            }
        });
        thread.start();
        Thread.sleep(2000); // 避免不打印Running
        thread.interrupt(); // interrupt() ⽅法⽤于通知线程停⽌，但不会直接终⽌线程，需要线程⾃⾏处理中断标志。
        // stop() ⽅法⽤来强制停⽌线程，⽬前已经处于废弃状态，因为 stop() ⽅法可能会在不⼀致的状态下释放锁，破坏对象的⼀致性。
    }
}
