package com.yupi.yuhaigui.model;

import com.yupi.yuhaigui.database.mapper.ChatMessageMapper;
import com.yupi.yuhaigui.database.mapper.ChatRoomMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.Data;

import java.util.*;

/**
 * 聊天房间
 */
@Data
public class ChatRoom{
    private String username;

    private Long roomId;

    private List<ChatMessage> chatMessageList;

    public ChatRoom(Long roomId, List<ChatMessage> chatMessageList) {
        this("guest", roomId, chatMessageList);
    }

    public ChatRoom(String username, Long roomId, List<ChatMessage> chatMessageList) {
        this.username = username;
        this.roomId = roomId;
        this.chatMessageList = chatMessageList;
    }

    public String getUsername() {
        return username;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public static Integer save(ChatMessageMapper chatMessageMapper, ChatRoomMapper chatRoomMapper, ChatRoom chatRoom) {
        com.yupi.yuhaigui.database.model.ChatRoom chatRoomTable = new com.yupi.yuhaigui.database.model.ChatRoom(
                chatRoom.username, chatRoom.roomId);
        chatRoomMapper.insert(chatRoomTable);
        Integer sessionId = chatRoomTable.getSessionId();
        for (int i = 0; i < chatRoom.getChatMessageList().size(); i++) {
            ChatMessage chatMessage = chatRoom.getChatMessageList().get(i);
            chatMessageMapper.insert(new com.yupi.yuhaigui.database.model.ChatMessage(
                    sessionId, i, chatMessage.type(), chatMessage.toString()));
        }
        return sessionId;
    }

    public static Map<Long, ChatRoom> load(ChatMessageMapper chatMessageMapper, ChatRoomMapper chatRoomMapper, String username) {
        Map<Long, ChatRoom> chatRoomList = new HashMap<>();
        List<com.yupi.yuhaigui.database.model.ChatRoom> chatRoomTables = chatRoomMapper.selectByUsername(username);
        for(com.yupi.yuhaigui.database.model.ChatRoom chatRoomTable : chatRoomTables) {
            List<ChatMessage> chatMessageList = new ArrayList<>();
            List<com.yupi.yuhaigui.database.model.ChatMessage> chatMessageTables = chatMessageMapper.selectBySessionId(chatRoomTable.getSessionId());
            for(com.yupi.yuhaigui.database.model.ChatMessage chatMessageTable : chatMessageTables) {
                if (chatMessageTable.getType() == dev.langchain4j.data.message.ChatMessageType.USER) {
                    chatMessageList.add(UserMessage.userMessage(chatMessageTable.getMessage()));
                } else if (chatMessageTable.getType() == dev.langchain4j.data.message.ChatMessageType.AI) {
                    chatMessageList.add(AiMessage.aiMessage(chatMessageTable.getMessage()));
                } else if (chatMessageTable.getType() == dev.langchain4j.data.message.ChatMessageType.SYSTEM) {
                    chatMessageList.add(SystemMessage.systemMessage(chatMessageTable.getMessage()));
                }
            }
            ChatRoom chatRoom = new ChatRoom(username, chatRoomTable.getRoomId(), chatMessageList);
            chatRoomList.put(chatRoomTable.getRoomId(), chatRoom);
        }
        return chatRoomList;
    }

    public static Integer insert(ChatMessageMapper chatMessageMapper, ChatRoomMapper chatRoomMapper,
                                 ChatRoom chatRoom, ChatMessage newChatMessage) {
        com.yupi.yuhaigui.database.model.ChatRoom chatRoomTable = chatRoomMapper.selectByUsernameAndRoomId(
                chatRoom.username, chatRoom.roomId
        );
        if (chatRoomTable == null) {
            return null;
        }
        Integer sessionId = chatRoomTable.getSessionId();
        chatMessageMapper.insert(new com.yupi.yuhaigui.database.model.ChatMessage(
                sessionId, chatRoom.getChatMessageList().size(),
                newChatMessage.type(), newChatMessage.toString()
        ));
        chatRoom.getChatMessageList().add(newChatMessage);
        return sessionId;
    }
}
