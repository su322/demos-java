package io.github.su322.parallelaccessoptimizationdemo.service.impl;

import io.github.su322.parallelaccessoptimizationdemo.service.RecommendService;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {
    @Override
    public List<String> getRecommend() {
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        return Arrays.asList("推荐1", "推荐2", "推荐3");
    }
}

