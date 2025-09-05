package io.github.su322.parallelaccessoptimizationdemo.service.impl;

import io.github.su322.parallelaccessoptimizationdemo.service.UserService;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public List<String> getUser() {
        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
        return Arrays.asList("用户A", "用户B");
    }
}

