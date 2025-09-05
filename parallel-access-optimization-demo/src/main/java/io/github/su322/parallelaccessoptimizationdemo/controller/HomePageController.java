package io.github.su322.parallelaccessoptimizationdemo.controller;

import io.github.su322.parallelaccessoptimizationdemo.service.HomePageService;
import io.github.su322.parallelaccessoptimizationdemo.vo.HomePageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomePageController {
    @Autowired
    private HomePageService homePageService;

    @GetMapping("/origin")
    public HomePageVO origin() {
        return homePageService.getHomePageOrigin();
    }

    @GetMapping("/cf")
    public HomePageVO cf() {
        return homePageService.getHomePageCF();
    }

    @GetMapping("/tp")
    public HomePageVO tp() {
        return homePageService.getHomePageThreadPool();
    }

    @GetMapping("/vt")
    public HomePageVO vt() {
        return homePageService.getHomePageVirtualThread();
    }
}
