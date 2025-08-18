package io.github.su322.redissondemo.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class LockTestController {
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private Environment environment;

    @GetMapping("/lock/test")
    public String testLock() {
        String port = environment.getProperty("local.server.port");

        String lockKey = "distributed-lock-demo";
        RLock lock = redissonClient.getLock(lockKey);

        String threadInfo = Thread.currentThread().getName();
        long start = System.currentTimeMillis();
        try {
            // 尝试获取锁，最多等待3秒，锁自动释放时间10秒
            boolean acquired = lock.tryLock(3, 10, TimeUnit.SECONDS);
            long acquiredTime = System.currentTimeMillis();
            if (acquired) {
                // 模拟业务处理8秒
                Thread.sleep(8000);
                long end = System.currentTimeMillis();
                return "锁已获得，端口: " + port + ", 线程: " + threadInfo +
                        ", 获取锁耗时: " + (acquiredTime - start) + " ms, 总耗时: " + (end - start) + " ms";
            } else {
                long end = System.currentTimeMillis();
                return "未获得锁，端口: " + port + ", 线程: " + threadInfo +
                        ", 获取锁耗时: " + (acquiredTime - start) + " ms, 总耗时: " + (end - start) + " ms";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "异常: " + e.getMessage();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("锁已释放，线程: " + threadInfo);
            }
        }
    }
}
