package concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 可以去看并行访问优化demo，里面有用到
 */

public class CompletableFutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 异步执行任务
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello from CompletableFuture!";
        });

        // thenApply：对结果进行转换
        CompletableFuture<String> transformed = future.thenApply(result -> result + " -- transformed");

        // thenAccept：消费结果
        transformed.thenAccept(result -> System.out.println("最终结果: " + result));

        // get() 阻塞等待结果
        String value = transformed.get();
        System.out.println("get()获取到的结果: " + value);
    }
}
