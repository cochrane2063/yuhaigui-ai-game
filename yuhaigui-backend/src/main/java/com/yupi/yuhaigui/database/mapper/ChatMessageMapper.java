package com.yupi.yuhaigui.database.mapper;

import com.yupi.yuhaigui.database.model.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper{
    int insert(ChatMessage chatMessage);
    ChatMessage selectById(@Param("messageId") Integer messageId);

    List<ChatMessage> selectBySessionId(@Param("sessionId")Integer sessionId);

    int update(ChatMessage entity);

    int deleteById(@Param("messageId") Integer messageId);
}
