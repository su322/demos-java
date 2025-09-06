package io.github.su322.llmdemo.service;

import io.github.su322.llmdemo.enums.LLMTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LLMChatServiceFactory {
    private static Map<String, ChatService> chatServiceMap;

    // Spring 自动注入所有 ChatService 实现
    @Autowired
    public LLMChatServiceFactory(Map<String, ChatService> chatServiceMap) {
        LLMChatServiceFactory.chatServiceMap = chatServiceMap;
    }

    // 从模型选择具体service
    public static ChatService getChatService(LLMTypeEnum type) {
        // Bean 名约定：QwenChatServiceImpl -> qwenChatServiceImpl，GLMChatServiceImpl -> glmChatServiceImpl
        String beanName = switch (type) {
            case QWEN_FLASH -> "qwenChatServiceImpl";
            case GLM_4_5_FLASH -> "glmChatServiceImpl";
            // 如果你用的是 switch 表达式（Java 14+），且所有枚举值都被 case 覆盖，编译器会提示 default 分支不必要，但如果你后续新增枚举值，编译器会强制你补全 case 或加 default。
            default -> throw new IllegalArgumentException("不支持的模型类型: " + type);
        };
        // 要实现“用注解或配置自动关联 bean 和枚举，避免 switch”，可以采用如下最佳实践：
        // 方案说明
        // 1.自定义注解
        // 定义一个注解（如 @LLMTypeMapping），用于标注每个 ChatService 实现类所对应的 LLMTypeEnum。
        // 2.实现类加注解
        // 在 GLMChatServiceImpl、QwenChatServiceImpl 等实现类上加上 @LLMTypeMapping(LLMTypeEnum.GLM_4_5_FLASH) 这样的注解。
        // 3.工厂自动扫描注解
        // 工厂在构造时遍历所有 ChatService Bean，读取注解，将 LLMTypeEnum 和 Bean 关联到 Map<LLMTypeEnum, ChatService>，无需 switch。
        // 4.获取服务时直接用枚举查 Map
        // 这样新增模型只需加实现类和注解，无需改工厂代码，完全消除硬编码和 switch。
        ChatService service = chatServiceMap.get(beanName);
        if (service == null) {
            throw new IllegalArgumentException("未找到模型实现: " + beanName);
        }
        return service;
    }
}
