package com.yupi.yuhaigui.manager;

import cn.hutool.core.collection.CollUtil;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 调用工具类
 */
@Service
public class AiManager {

    // AI 调用客服端
    @Resource
    private ChatModel chatModel;

    /**
     * 聊天（只允许传入系统预设和用户预设）
     *
     * @param systemPrompt
     * @param userPrompt
     * @return
     */
    public AiMessage doChat(String systemPrompt, String userPrompt) {
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = SystemMessage.systemMessage(systemPrompt);
        final ChatMessage userMessage = UserMessage.userMessage(userPrompt);
        messages.add(systemMessage);
        messages.add(userMessage);
        return doChat(messages);
    }

    /**
     * 更通用的方法，允许用户传入任意条消息列表
     *
     * @param chatMessageList
     * @return
     */
    public AiMessage doChat(List<ChatMessage> chatMessageList) {
        ChatResponse chatResponse = chatModel.chat(chatMessageList);
        AiMessage aiMessage = chatResponse.aiMessage();
        System.out.println("AI 返回内容：" + aiMessage.text());
        return aiMessage;
    }
}
