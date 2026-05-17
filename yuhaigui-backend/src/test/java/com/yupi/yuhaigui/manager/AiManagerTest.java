package com.yupi.yuhaigui.manager;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String systemPrompt = "你是一位程序员大佬";
        String userPrompt = "帮我写一个java程序";
        String answer = aiManager.doChat(systemPrompt, userPrompt).text();
        System.out.println(answer);
    }
}