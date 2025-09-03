package concurrency.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingQueueExample {
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);

        // 生产者线程
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    queue.put(i);
                    System.out.println("生产者生产: " + i + ", 当前队列长度: " + queue.size());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // 消费者线程
        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    int item = queue.take(); // 如果队列为空，take 方法会阻塞当前线程，直到队列中有元素可用为止，然后返回并移除队列头部元素
                    System.out.println("消费者消费: " + item + ", 当前队列长度: " + queue.size());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("队列已空: " + queue.isEmpty());
    }
}
