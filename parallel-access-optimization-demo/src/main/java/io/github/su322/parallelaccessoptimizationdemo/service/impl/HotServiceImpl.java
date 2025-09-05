package io.github.su322.parallelaccessoptimizationdemo.service.impl;

import io.github.su322.parallelaccessoptimizationdemo.service.HotService;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class HotServiceImpl implements HotService {
    @Override
    public List<String> getHot() {
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        return Arrays.asList("热榜1", "热榜2", "热榜3");
    }
}

