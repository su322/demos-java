package io.github.su322.llmdemo.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import io.github.su322.llmdemo.enums.LLMTypeEnum;
import io.github.su322.llmdemo.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("glmChatServiceImpl") // 指定bean名让工厂能找到
public class GLMChatServiceImpl implements ChatService {
    // 这里硬编码 随便写的
    private final String apiKey = "your-api-key";

    @Override
    public String chat(LLMTypeEnum type, String prompt) {
        // 初始化客户端 每次请求都要重新初始化，耗时略长，是个可以优化的点，不过我写着玩的就不管了
        ZhipuAiClient client = ZhipuAiClient.builder()
                .apiKey(apiKey)
                .build();

        // 创建聊天完成请求
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(type.getName())
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content(prompt) // 用户输入的 prompt
                                .build()
                ))
                .thinking(ChatThinking.builder().type("enabled").build())
                .maxTokens(4096)
                .temperature(0.6f)
                .build();

        // 发送请求
        ChatCompletionResponse response = client.chat().createChatCompletion(request);

        // 获取回复
        if (response.isSuccess()) {
            String reply = response.getData().getChoices().get(0).getMessage().getContent().toString();
            return "[GLM回复] " + reply;
        } else {
            return "错误: " + response.getMsg();
        }
    }
}
