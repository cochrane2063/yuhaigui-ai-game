package com.yupi.yuhaigui.database.mapper;

import com.yupi.yuhaigui.database.model.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomMapper{
    int insert(ChatRoom entity);

    ChatRoom selectById(@Param("sessionId") Integer sessionId);

    List<ChatRoom> selectByUsername(@Param("username") String username);

    ChatRoom selectByUsernameAndRoomId(@Param("username") String username, @Param("roomId") Long roomId);

    int update(ChatRoom entity);

    int deleteById(@Param("sessionId") Integer sessionId);
}
