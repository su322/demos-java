package concurrency.lock;

/**
 * synchronized 支持可重入示例
 */

public class ReentrantSynchronizedExample {
    public static void main(String[] args) {
        ReentrantSynchronizedExample example = new ReentrantSynchronizedExample();
        example.method1();
    }

    private synchronized void method1() {
        System.out.println(Thread.currentThread().getName() + " 进入 method1，获得锁");
        method2();
    }

    private synchronized void method2() {
        System.out.println(Thread.currentThread().getName() + " 进入 method2，重入锁成功");
    }
}
