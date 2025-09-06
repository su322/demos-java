package io.github.su322.llmdemo.service.impl;

import io.github.su322.llmdemo.enums.LLMTypeEnum;
import io.github.su322.llmdemo.service.ChatService;
import org.springframework.stereotype.Service;

@Service("qwenChatServiceImpl")
public class QwenChatServiceImpl implements ChatService {
    @Override
    public String chat(LLMTypeEnum type, String prompt) {
        // 这里是模拟实现，后续可接入真实Qwen模型
        return "[Qwen回复] 模拟回复";
    }
}
