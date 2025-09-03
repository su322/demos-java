package concurrency.concurrenthashmap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap 示例：
 * 演示多线程并发写入和读取，保证线程安全。
 * <p>
 * 与 HashMapUnsafeExample 对比：不会出现数据丢失、遍历异常，map.size() 始终正确。
 * Reader-2 遍历前 map.size(): 0
 * Reader-1 遍历前 map.size(): 0
 * Reader-0 遍历前 map.size(): 0
 * Reader-0 遍历过程中实际遍历到的key数量: 509, 遍历结束后 map.size(): 912
 * Reader-2 遍历过程中实际遍历到的key数量: 509, 遍历结束后 map.size(): 872
 * Reader-1 遍历过程中实际遍历到的key数量: 505, 遍历结束后 map.size(): 789
 * 所有写入完成，map大小: 50000
 * ConcurrentHashMap 示例结束
 */

public class ConcurrentHashMapExample {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
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
                    // 有时间差，还在不断写入，count和mapSizeAfter确实是不一样的，我在想这个count是不是没什么用
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
        System.out.println("ConcurrentHashMap 示例结束");
    }
}
