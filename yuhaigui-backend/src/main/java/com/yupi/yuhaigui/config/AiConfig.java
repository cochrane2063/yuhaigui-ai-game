package com.yupi.yuhaigui.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    @Value("${ai.apiKey}")
    private String apiKey;

    @Value("${ai.baseUrl}")
    private String baseUrl;

    @Value("${ai.model}")
    private String model;

    /**
     * 初始化 AI 客户端
     * @return
     */
    @Bean
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(model)
                .returnThinking(true)
                .build();
    }
}
