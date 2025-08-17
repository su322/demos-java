package concurrency;

import java.util.concurrent.Exchanger;

/**
 * 具体实现原理（简化版）：
 * Exchanger 内部维护一个“槽位”或“节点”来存放第一个线程的数据。
 * 第一个线程到达时，把自己的数据放入槽位，然后阻塞等待第二个线程。
 * 第二个线程到达时，发现槽位已被占用，就把自己的数据和槽位里的数据交换，然后唤醒第一个线程。
 * 两个线程都拿到对方的数据，exchange() 方法返回。
 */

public class ExchangerExample {
    public static void main(String[] args) throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();

        Thread threadA = new Thread(() -> {
            try {
                String dataA = "来自A的数据";
                System.out.println("线程A交换前: " + dataA);
                String received = exchanger.exchange(dataA); // 这里
                System.out.println("线程A交换后: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                String dataB = "来自B的数据";
                System.out.println("线程B交换前: " + dataB);
                String received = exchanger.exchange(dataB); // 这里
                System.out.println("线程B交换后: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadA.start();
        threadB.start();
    }
}
