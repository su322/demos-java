package concurrency.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 三大特性演示：
 * 1. lock.lockInterruptibly() 可中断锁获取
 *   与普通的 lock() 方法不同，lockInterruptibly() 允许线程在等待锁时被其他线程中断。
 *   如果线程在等待锁期间被中断（比如调用 thread.interrupt()），它会抛出 InterruptedException，线程可以根据业务需要做出响应（如退出、重试等），避免死等。
 *   适用于那些不希望线程无限期阻塞等待锁的场景，比如需要及时响应取消、超时等需求。
 * 2. lock.tryLock() 尝试获取锁
 * 3. 公平锁/非公平锁
 */
public class ReentrantLockFeaturesExample {
    private final ReentrantLock lock = new ReentrantLock(); // 1
    private final ReentrantLock tryLock = new ReentrantLock(); // 2
    private final ReentrantLock fairLock = new ReentrantLock(true); // 3 公平锁
    private final ReentrantLock unfairLock = new ReentrantLock(false); // 3 非公平锁

    // 1. 可中断锁获取示例
    public void interruptibleLockDemo() {
        Thread t1 = new Thread(() -> {
            try {
                System.out.println("t1 尝试获取锁...");
                lock.lockInterruptibly();
                System.out.println("t1 获取到锁");
                Thread.sleep(5000); // 持续持有锁
            } catch (InterruptedException e) {
                System.out.println("t1 被中断，未能获取锁");
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                System.out.println("t2 尝试获取锁...");
                lock.lockInterruptibly(); // 关键，不能是lock()
                // 当线程调用 lock() 获取锁时，如果锁被其他线程持有，当前线程会一直阻塞等待，直到获得锁为止。
                // 在阻塞期间，即使其他线程对该线程调用 interrupt()，该线程也不会响应中断信号，依然会继续等待锁，直到获取到锁才会恢复执行。
                System.out.println("t2 获取到锁");
            } catch (InterruptedException e) {
                System.out.println("t2 被中断，未能获取锁");
            } finally {
                // 如果 t2 在 finally 里直接 unlock，而没有获得锁，会抛出 IllegalMonitorStateException 异常，导致线程异常终止。这是错误的用法，建议始终加上持有锁判断。
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });

        t1.start();
        t2.start();
        try {
            t2.interrupt(); // 关键，中断 t2，此时 t1 持有锁，使用 lockInterruptibly() 的线程 t2 就会中断，抛出 InterruptedException
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 2. 尝试获取锁示例
    public void tryLockDemo() {
        Thread t1 = new Thread(() -> {
            try {
                tryLock.lock();
                System.out.println("t1 获取到锁，执行任务...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                tryLock.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                System.out.println("t2 尝试 tryLock...");
                // 让线程在指定时间内尝试获取锁，超时后自动放弃，提升系统响应性和健壮性，适合需要避免长时间阻塞的并发场景
                if (tryLock.tryLock(1, TimeUnit.SECONDS)) {
                    System.out.println("t2 获取到锁");
                    tryLock.unlock();
                } else {
                    System.out.println("t2 未能获取到锁");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 3. 公平锁/非公平锁示例
    public void fairUnfairLockDemo() {
        // 公平锁保证线程获取锁的顺序是先到先得，即按照线程请求锁的顺序来分配锁。
        // 优点：避免线程“饥饿”，保证每个线程最终都能获得锁。
        // 缺点：性能略低，因为需要维护一个队列来记录等待顺序。

        // 当然，锁是在最上面创建的，我只是在这写个注释
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            // 公平锁会维护一个队列，保证线程按照请求锁的顺序依次获得锁。
            // 第一次循环时，三个线程依次获得锁（0、1、2），每个线程获得锁后立即释放，队列里下一个线程再获得锁。
            // 第二次循环时，三个线程再次竞争锁，公平锁依然按照排队顺序分配锁，所以还是 0、1、2。

            // 注意，公平锁的队列不是“走完又从头开始”，而是每次有新的锁请求就重新排队。
            // 因为你的线程启动顺序和请求锁的时间几乎一致，所以每次循环排队顺序都一样。
            // 不过如果线程请求锁的时间有差异，顺序可能会变化，但公平锁始终保证“先请求先获得”。
            for (int i = 0; i < 2; i++) {
                fairLock.lock();
                try {
                    System.out.println("[公平锁] " + threadName + " 获得锁");
                } finally {
                    fairLock.unlock(); // 循环一次就释放了，如果你希望某个线程一直持有锁，可以把 unlock 移到 for 循环外，但这就不是公平锁的典型用法了。
                }
            }
        };
        /*
        公平锁测试：
        [公平锁] 公平线程-0 获得锁
        [公平锁] 公平线程-1 获得锁
        [公平锁] 公平线程-2 获得锁
        [公平锁] 公平线程-0 获得锁
        [公平锁] 公平线程-1 获得锁
        [公平锁] 公平线程-2 获得锁
         */

        // 非公平锁不保证线程获取锁的顺序，允许插队。在 Java 中，new ReentrantLock(false) 或默认构造方法创建的是非公平锁。
        // 优点：性能更高，减少线程切换和调度的开销。
        // 缺点：可能导致某些线程长时间得不到锁，出现“线程饥饿”现象。
        Runnable unfairTask = () -> {
            String threadName = Thread.currentThread().getName();
            for (int i = 0; i < 2; i++) {
                unfairLock.lock();
                try {
                    System.out.println("[非公平锁] " + threadName + " 获得锁");
                } finally {
                    unfairLock.unlock();
                }
            }
        };
        /*
        非公平锁测试：
        [非公平锁] 非公平线程-0 获得锁
        [非公平锁] 非公平线程-0 获得锁
        [非公平锁] 非公平线程-1 获得锁
        [非公平锁] 非公平线程-1 获得锁
        [非公平锁] 非公平线程-2 获得锁
        [非公平锁] 非公平线程-2 获得锁
        */

        System.out.println("公平锁测试：");
        for (int i = 0; i < 3; i++) {
            new Thread(task, "公平线程-" + i).start();
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("非公平锁测试：");
        for (int i = 0; i < 7; i++) { // 写少了还不好看出他是乱序的
            new Thread(unfairTask, "非公平线程-" + i).start();
        }
    }

    public static void main(String[] args) {
        ReentrantLockFeaturesExample demo = new ReentrantLockFeaturesExample();
        System.out.println("--- 可中断锁获取演示 ---");
//        demo.interruptibleLockDemo();
        System.out.println("--- 尝试获取锁演示 ---");
//        demo.tryLockDemo();
        System.out.println("--- 公平锁/非公平锁演示 ---");
        demo.fairUnfairLockDemo();
    }
}

