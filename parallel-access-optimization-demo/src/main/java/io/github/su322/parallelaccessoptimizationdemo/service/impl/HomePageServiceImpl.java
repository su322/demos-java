package io.github.su322.parallelaccessoptimizationdemo.service.impl;

import io.github.su322.parallelaccessoptimizationdemo.service.*;
import io.github.su322.parallelaccessoptimizationdemo.vo.HomePageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class HomePageServiceImpl implements HomePageService {
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private HotService hotService;
    @Autowired
    private AdService adService;
    @Autowired
    private UserService userService;

    /**
     * 串行方式，依次调用每个板块的 service，全部顺序执行。
     * 总耗时为所有 service 方法耗时之和，适合低并发、无性能要求场景。
     */
    @Override
    public HomePageVO getHomePageOrigin() {
        long start = System.currentTimeMillis();
        HomePageVO vo = HomePageVO.builder()
                .recommend(recommendService.getRecommend())
                .hot(hotService.getHot())
                .ad(adService.getAd())
                .user(userService.getUser())
                .costMillis(System.currentTimeMillis() - start)
                .build();
        return vo;
    }

    /**
     * 使用 CompletableFuture 并发拉取各板块数据，底层用默认线程池（ForkJoinPool）。
     * 四个任务并发执行，主线程依次 get 结果，总耗时约等于最慢的 service 方法耗时。
     * 适合对响应速度有要求的场景。
     * <p>
     * 在 Java 中，CompletableFuture.supplyAsync() 默认使用的线程池是 ForkJoinPool.commonPool()。
     * 这个线程池是全局共享的，所有未指定线程池的 CompletableFuture、parallelStream 等都会用它。
     * 如果有大量阻塞任务（如 sleep、IO），容易导致线程池被“卡满”，新任务排队，响应变慢。在我的服务器上就出现了。
     */
    @Override
    public HomePageVO getHomePageCF() {
        long start = System.currentTimeMillis();
        CompletableFuture<List<String>> recommendFuture = CompletableFuture.supplyAsync(recommendService::getRecommend);
        CompletableFuture<List<String>> hotFuture = CompletableFuture.supplyAsync(hotService::getHot);
        CompletableFuture<List<String>> adFuture = CompletableFuture.supplyAsync(adService::getAd);
        CompletableFuture<List<String>> userFuture = CompletableFuture.supplyAsync(userService::getUser);
        try {
            HomePageVO vo = HomePageVO.builder()
                    .recommend(recommendFuture.get()) // 主线程阻塞，直到 recommend 结果返回
                    .hot(hotFuture.get())
                    .ad(adFuture.get())
                    .user(userFuture.get())
                    .costMillis(System.currentTimeMillis() - start)
                    .build();
            return vo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用 CompletableFuture 并发拉取各板块数据，底层用自定义线程池。
     * “自定义线程池”通常指的是不是用 JDK 默认的全局线程池（如 ForkJoinPool.commonPool()），而是你自己通过代码显式创建的线程池实例。
     * 线程池大小可控，适合高并发生产环境，防止线程资源耗尽。
     * 总耗时约等于最慢的 service 方法耗时。
     */
    @Override
    public HomePageVO getHomePageThreadPool() {
        long start = System.currentTimeMillis();
        // 只要你在 try(...) 里声明的对象实现了 AutoCloseable（或 Closeable）接口，try 代码块执行完毕后（无论是正常结束还是抛出异常），都会自动调用该对象的 close() 方法。
        // 线程池（如 ExecutorService）从 Java 7 开始实现了 AutoCloseable，其 close() 方法内部会调用 shutdown()，从而优雅关闭线程池。
        try (ExecutorService threadPool = Executors.newFixedThreadPool(4)) {
            CompletableFuture<List<String>> recommendFuture = CompletableFuture.supplyAsync(recommendService::getRecommend, threadPool);
            CompletableFuture<List<String>> hotFuture = CompletableFuture.supplyAsync(hotService::getHot, threadPool);
            CompletableFuture<List<String>> adFuture = CompletableFuture.supplyAsync(adService::getAd, threadPool);
            CompletableFuture<List<String>> userFuture = CompletableFuture.supplyAsync(userService::getUser, threadPool);
            HomePageVO vo = HomePageVO.builder()
                    .recommend(recommendFuture.get())
                    .hot(hotFuture.get())
                    .ad(adFuture.get())
                    .user(userFuture.get())
                    .costMillis(System.currentTimeMillis() - start)
                    .build();
            return vo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用 Java 21+ 虚拟线程并发拉取各板块数据。
     * 每个任务分配一个虚拟线程，极高并发能力，资源消耗极低。
     * 推荐在新项目或高并发场景下使用。
     * 看VirtualThreadExample注释
     * <p>
     * 传统线程池（如 newFixedThreadPool）受限于操作系统线程数量，线程切换和调度开销大。
     * 虚拟线程池几乎没有线程数量限制，极大提升并发能力和资源利用率。
     * <p>
     * 100并发量对于4核机器来说，已经能让CPU饱和。
     * 自定义线程池（比如4~8个线程）和虚拟线程（100个虚拟线程）在这种资源瓶颈下，最终都要排队等CPU，性能差距不会太大。
     * 如果你的任务是“阻塞型”（如sleep、IO），虚拟线程优势会更明显，但在小型服务器上，优势有限。
     * 虚拟线程在遇到阻塞操作（如 sleep、IO、锁等待等）时，不会占用宝贵的操作系统线程（平台线程），而是“挂起”自己，平台线程可以去执行其他虚拟线程。
     * 在小型服务器、并发量不高时，虚拟线程和自定义线程池差距不大，但在大规模阻塞并发下优势明显。
     */
    @Override
    public HomePageVO getHomePageVirtualThread() {
        long start = System.currentTimeMillis();
        // 每次提交任务时都会为该任务分配一个新的虚拟线程
        try (ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<List<String>> recommendFuture = vts.submit(recommendService::getRecommend);
            Future<List<String>> hotFuture = vts.submit(hotService::getHot);
            Future<List<String>> adFuture = vts.submit(adService::getAd);
            Future<List<String>> userFuture = vts.submit(userService::getUser);
            HomePageVO vo = HomePageVO.builder()
                    .recommend(recommendFuture.get())
                    .hot(hotFuture.get())
                    .ad(adFuture.get())
                    .user(userFuture.get())
                    .costMillis(System.currentTimeMillis() - start)
                    .build();
            return vo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
