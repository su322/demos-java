package io.github.su322.parallelaccessoptimizationdemo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomePageVO {
    private List<String> recommend;
    private List<String> hot;
    private List<String> ad;
    private List<String> user;
    private long costMillis;
}

