package io.github.su322.llmdemo.enums;

import lombok.Getter;

@Getter
public enum LLMTypeEnum {
    GLM_4_5_FLASH("glm", "glm-4.5-flash"),
    QWEN_FLASH("qwen", "qwen-flash");

    private final String code;
    private final String name;

    LLMTypeEnum(String code, String name) {
        this.code = code; // 简单取个代号，当然如果模型多了还得想想
        this.name = name;
    }

    public static LLMTypeEnum fromCode(String code) {
        for (LLMTypeEnum t : values()) {
            if (t.getCode().equalsIgnoreCase(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException("不支持的模型code: " + code);
    }
}
