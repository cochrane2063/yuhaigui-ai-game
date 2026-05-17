package com.yupi.yuhaigui;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class AiTest {

    @Value("${ai.apiKey}")
    private String apiKey;

    @Value("${ai.baseUrl}")
    private String baseUrl;

    @Value("${ai.model}")
    private String model;

    @Test
    public void doTest() {
        // 此为默认路径，您可根据业务所在地域进行配置
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(model)
                .returnThinking(true)
                .build();

        System.out.println("\n----- standard request -----");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = SystemMessage.systemMessage("你是人工智能助手.");
        final ChatMessage userMessage = UserMessage.userMessage("谁是程序员鱼皮？");
        messages.add(systemMessage);
        messages.add(userMessage);

        // 单次调用
        ChatResponse chatResponse = chatModel.chat(messages);

        System.out.println(chatResponse.aiMessage().text());

        // 流式调用
        System.out.println("\n----- streaming request -----");
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = SystemMessage.systemMessage("你是人工智能助手.");
        final ChatMessage streamUserMessage = UserMessage.userMessage("常见的十字花科植物有哪些？");
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);

    }
}
