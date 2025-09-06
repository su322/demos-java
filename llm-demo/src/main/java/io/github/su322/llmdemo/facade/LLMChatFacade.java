package io.github.su322.llmdemo.facade;

import io.github.su322.llmdemo.enums.LLMTypeEnum;
import io.github.su322.llmdemo.service.ChatService;
import io.github.su322.llmdemo.service.LLMChatServiceFactory;
import org.springframework.stereotype.Component;

@Component
public class LLMChatFacade {
    /**
     * 统一对外的聊天接口
     * @param type LLM模型类型
     * @param prompt 用户输入
     * @return 模型回复
     */
    public String chat(LLMTypeEnum type, String prompt) {
        ChatService service = LLMChatServiceFactory.getChatService(type);
        return service.chat(type, prompt);
    }
}
