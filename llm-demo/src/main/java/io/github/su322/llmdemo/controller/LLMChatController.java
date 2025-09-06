package io.github.su322.llmdemo.controller;

import io.github.su322.llmdemo.enums.LLMTypeEnum;
import io.github.su322.llmdemo.facade.LLMChatFacade;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/llm")
public class LLMChatController {
    @Autowired
    private LLMChatFacade llmChatFacade;

    /**
     * 聊天接口
     * @param code 模型代号（如 "glm"、"qwen"）
     * @param prompt 用户输入
     * @return 模型回复
     */
    @PostMapping("/chat")
    public String chat(@RequestParam String code, @RequestParam String prompt) {
        LLMTypeEnum type = LLMTypeEnum.fromCode(code); // 从代号转换到枚举对象
        return llmChatFacade.chat(type, prompt);
    }
}
