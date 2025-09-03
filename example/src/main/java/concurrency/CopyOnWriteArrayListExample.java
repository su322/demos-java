package concurrency;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayListExample {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new CopyOnWriteArrayList<>();

        // 写线程
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Writer started adding: item-" + i);
                try {
                    Thread.sleep(100); // 在这里加sleep满意了，Reader可以被包围在两个writer的sout中间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                list.add("item-" + i);
                System.out.println("Writer added: item-" + i);
            }
        });

        // 读线程
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Reader sees: " + list);
                try {
                    Thread.sleep(50); // 算是模拟了个读多写少吧
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        writer.start();
        reader.start();
        writer.join();
        reader.join();
        System.out.println("Final list: " + list);
    }
}
