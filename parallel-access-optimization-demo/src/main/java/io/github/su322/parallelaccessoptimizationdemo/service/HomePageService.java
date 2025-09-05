package io.github.su322.parallelaccessoptimizationdemo.service;

import io.github.su322.parallelaccessoptimizationdemo.vo.HomePageVO;

public interface HomePageService {
    HomePageVO getHomePageOrigin();
    HomePageVO getHomePageCF();
    HomePageVO getHomePageThreadPool();
    HomePageVO getHomePageVirtualThread();
}
