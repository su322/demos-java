package io.github.su322.fastexceldemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.github.su322.fastexceldemo.repository.mapper")
public class FastexcelDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastexcelDemoApplication.class, args);
    }

}
