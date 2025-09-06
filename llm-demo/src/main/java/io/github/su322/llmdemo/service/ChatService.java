package io.github.su322.llmdemo.service;

import io.github.su322.llmdemo.enums.LLMTypeEnum;

public interface ChatService {
    /**
     * 根据输入 prompt 和模型名返回模型回复
     * @param type   模型类型（如 glm-4.5-flash，从controller层的代号转化过来的）
     * @param prompt 用户输入
     * @return 模型回复
     */
    String chat(LLMTypeEnum type, String prompt);
}
