package concurrency.concurrenthashmap;

import java.util.HashMap;

/**
 * HashMapUnsafeExample
 * 演示普通HashMap在多线程并发写入和读取时的线程不安全问题。
 * 可能出现数据丢失、异常（如ConcurrentModificationException）、map.size()不正确等现象。
 * <p>
 * Reader-0 遍历前 map.size(): 5
 * Reader-2 遍历前 map.size(): 5
 * Reader-1 遍历前 map.size(): 5
 * Reader-0 遍历时发生异常: java.util.ConcurrentModificationException
 * Reader-1 遍历时发生异常: java.util.ConcurrentModificationException
 * Reader-2 遍历时发生异常: java.util.ConcurrentModificationException
 * 所有写入完成，map大小: 43922
 * HashMapUnsafeExample 示例结束
 */

public class HashMapUnsafeExample {
    public static void main(String[] args) throws InterruptedException {
        HashMap<Integer, String> map = new HashMap<>();
        int writerCount = 5;
        Thread[] threads = new Thread[writerCount];
        // 多线程并发写入
        for (int i = 0; i < writerCount; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    map.put(id * 100000 + j, "线程:" + id + " 值:" + j);
                    if (j % 100 == 0) {
                        try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                    }
                }
            }, "Writer-" + id);
            threads[i].start();
        }

        // 并发读取：写入过程中启动多个读线程
        int readerCount = 3;
        Thread[] readers = new Thread[readerCount];
        for (int i = 0; i < readerCount; i++) {
            readers[i] = new Thread(() -> {
                try {
//                    Thread.sleep(10);
                    int count = 0;
                    int mapSizeBefore = map.size();
                    System.out.println(Thread.currentThread().getName() + " 遍历前 map.size(): " + mapSizeBefore);
                    for (Integer k : map.keySet()) {
                        count++;
                        if (count % 500 == 0) {
                            Thread.sleep(1); // 增加冲突概率
                        }
                    }
                    int mapSizeAfter = map.size();
                    System.out.println(Thread.currentThread().getName() + " 遍历过程中实际遍历到的key数量: " + count + ", 遍历结束后 map.size(): " + mapSizeAfter);
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " 遍历时发生异常: " + e);
                }
            }, "Reader-" + i);
            readers[i].start();
        }
        // 等待所有写线程完成
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("所有写入完成，map大小: " + map.size());

        // 等待读线程完成
        for (Thread r : readers) {
            r.join();
        }
        System.out.println("HashMapUnsafeExample 示例结束");
    }
}
