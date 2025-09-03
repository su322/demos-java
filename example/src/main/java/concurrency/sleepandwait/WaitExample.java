package concurrency.sleepandwait;

/**
 * 这个是比另外的文件简洁一点的用法示例
 * <p>
 * 写到CountDownLatch时增注：
 * wait 用于低级对象锁同步，需配合 notify/notifyAll，容易出错。
 * await 用于高级并发工具类，语义清晰，推荐在实际开发中使用。
 */

class WaitExample {
    public static void main(String[] args) {
        final Object lock = new Object(); // 这个 lock 是 main 方法里的局部变量锁，只能被 main 方法及其创建的线程使用，不具备跨方法、跨实例同步能力。

        Thread thread = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("线程准备等待 2 秒");
                    lock.wait(2000); // 线程会等待2秒，或者直到其他线程调⽤lock.notify()/notifyAll()
                    System.out.println("线程结束等待");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
