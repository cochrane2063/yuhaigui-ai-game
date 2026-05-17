package com.yupi.yuhaigui.controller;


import com.yupi.yuhaigui.dto.ChatRoomDTO;
import com.yupi.yuhaigui.service.ChatService;
import jakarta.annotation.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 对话接口
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    /**
     * 与 AI 对话
     *
     * @param roomId  聊天室 ID
     * @param message 用户输入的消息
     * @return AI 的回复
     */
    @PostMapping("/{roomId}/send")
    public String doChat(@AuthenticationPrincipal String username, @PathVariable("roomId") long roomId, @RequestParam("message") String message) {
        return chatService.doChat(username, roomId, message);
    }

    /**
     * 获取所有聊天室列表
     *
     * @return 聊天室列表
     */
    @GetMapping("/rooms")
    public List<ChatRoomDTO> getChatRoomList(@AuthenticationPrincipal String username) {
        return chatService.getChatRoomList(username);
    }
}