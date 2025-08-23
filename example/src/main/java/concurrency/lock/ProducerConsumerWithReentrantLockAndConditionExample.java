package concurrency.lock;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLockConditionExample 演示了如何使用 ReentrantLock 和 Condition 实现生产者-消费者模型。
 * <p>
 * 主要功能：
 * 1. 使用 ReentrantLock 保证线程安全。
 * 2. 使用 Condition 实现队列满/空时的等待与唤醒。
 * 3. 生产者线程和消费者线程通过队列进行协作。
 * 4. sout 输出直观展示锁的获取、等待、唤醒、生产和消费过程。
 */

public class ProducerConsumerWithReentrantLockAndConditionExample {
    private static final int CAPACITY = 5;
    private final Queue<Integer> queue = new ArrayDeque<>(); // 不能直接指定容量

    // 生产者-消费者模型不一定必须用锁，但在多线程并发环境下，需要保证线程安全和数据一致性。
    // 如果使用了线程安全的阻塞队列（如 ArrayBlockingQueue、LinkedBlockingQueue），这些类内部已经实现了线程安全和阻塞机制，不需要额外加锁。
    // Java 推荐用 BlockingQueue，直接支持生产者-消费者模型，如果需要自定义同步逻辑或更复杂的条件控制，可以用锁和条件变量（如 ReentrantLock + Condition）。
    private final ReentrantLock lock = new ReentrantLock(); // 生产者消费者竞争同一把锁
    private final Condition producer = lock.newCondition(); // 这两个名字我改了，之前是 notFull 和 notEmpty
    private final Condition consumer = lock.newCondition();

    public void produce(int value) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == CAPACITY) {
                System.out.println(Thread.currentThread().getName() + " 队列已满，生产者等待...");
                producer.await(); // 释放锁并进入等待状态，直到被消费者唤醒
            }
            queue.offer(value);
            System.out.println(Thread.currentThread().getName() + " 生产了: " + value + "，队列大小: " + queue.size());
            consumer.signal();
        } finally {
            lock.unlock();
        }
    }

    public void consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " 队列为空，消费者等待...");
                consumer.await(); // 释放锁并进入等待状态，直到被生产者唤醒
            }
            int value = queue.poll();
            System.out.println(Thread.currentThread().getName() + " 消费了: " + value + "，队列大小: " + queue.size());
            producer.signal(); // 唤醒生产者 如果此时没有线程在 producer.await(); 处等待，signal() 就什么都不做，信号会被“丢弃”，不会被记录或积压。
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ProducerConsumerWithReentrantLockAndConditionExample example = new ProducerConsumerWithReentrantLockAndConditionExample();

        // 生产者线程
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    example.produce(i);
                    Thread.sleep(50); // 生产消费比通过睡眠时间改成大概4:1，因为我想看到其中一个等待的样子
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "生产者");

        // 消费者线程
        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    example.consume();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "消费者");

        producer.start();
        consumer.start();

        try {
            // 在 Java 中，main 线程结束后，JVM 并不会立刻退出，只要有其他非守护线程（如 producer、consumer）还在运行，JVM会等所有非守护线程结束后才退出。所以即使 main 线程提前结束，生产者和消费者线程依然会继续执行，输出也会完整。
            // join() 的主要作用是让 main 线程等待子线程执行完毕后再继续执行 main 线程后面的代码。如果 main 方法里没有后续需要依赖子线程结果的逻辑，确实可以不用 join。
            // 如果 main 方法里还有其他操作（比如统计、汇总、关闭资源等），就必须用 join 保证子线程都执行完毕。
            // 如果只是演示生产者-消费者模型，main 方法只负责启动线程，没有后续逻辑，join 可有可无。
            // 写了这么多注释，我决定保留这段代码，虽然在这里没有用处
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            // 这是标准写法，用于响应中断。如果主线程在等待期间被中断，会捕获异常并设置中断标志，保证线程的中断语义不丢失。
            // 虽然在你的场景下一般不会被中断，但这是良好的并发编程习惯。
            Thread.currentThread().interrupt();
        }
    }
}
