package io.github.su322.fastexceldemo.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface CommentService {
    void download2excel(HttpServletResponse response) throws IOException;

    void download2excelConcurrent(HttpServletResponse response) throws IOException, InterruptedException;

    void download2excelVirtualThread(HttpServletResponse response) throws IOException, InterruptedException;

    void batchInsert5Million();

    void deleteAllComments();

    long countComments();
}
