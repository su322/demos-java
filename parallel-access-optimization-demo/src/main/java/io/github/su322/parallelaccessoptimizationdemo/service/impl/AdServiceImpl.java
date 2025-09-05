package io.github.su322.parallelaccessoptimizationdemo.service.impl;

import io.github.su322.parallelaccessoptimizationdemo.service.AdService;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class AdServiceImpl implements AdService {
    @Override
    public List<String> getAd() {
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        return Arrays.asList("广告1", "广告2");
    }
}

