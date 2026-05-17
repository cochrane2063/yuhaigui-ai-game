package com.yupi.yuhaigui.dto;

import com.yupi.yuhaigui.model.ChatRoom;
import dev.langchain4j.data.message.ChatMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRoomDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Long roomId;
    private List<String[]> messageList;

    public static ChatRoomDTO from(ChatRoom room) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.username = room.getUsername();
        dto.roomId = room.getRoomId();
        dto.messageList = new ArrayList<String[]>();
        for (ChatMessage chatMessage : room.getChatMessageList()) {
            dto.messageList.add(new String[]{chatMessage.type().name(), chatMessage.toString()});
        }
        return dto;
    }
}
